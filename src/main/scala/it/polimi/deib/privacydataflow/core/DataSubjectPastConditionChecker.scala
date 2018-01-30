package it.polimi.deib.privacydataflow.core

import java.lang.reflect.Field

import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by miik on 12/08/17.
  */
class DataSubjectPastConditionChecker[T,S] extends RichCoFlatMapFunction[(String, T, scala.collection.mutable.ListBuffer[Boolean]), (String, S), (String, T, scala.collection.mutable.ListBuffer[Boolean])] {

  private var lastOtherStreamValues: scala.collection.mutable.Map[String, (List[(String, S)], PastCondition, Boolean)] = scala.collection.mutable.Map[String, (List[(String, S)], PastCondition, Boolean)]()

  val log: Logger = LoggerFactory.getLogger(classOf[DataSubjectPastConditionChecker[T,S]])

  def setPastConditions(conds: scala.collection.mutable.Map[String, PastCondition]): Unit = {
    for(c <- conds.keys){
      this.lastOtherStreamValues += c -> (List(), conds(c), false)
    }
  }

  override def flatMap1(in1: (String, T, scala.collection.mutable.ListBuffer[Boolean]), out: Collector[(String, T, scala.collection.mutable.ListBuffer[Boolean])]) = {

    val f: Field = in1._2.getClass.getDeclaredField("dataSubject")
    f.setAccessible(true)
    if (lastOtherStreamValues.contains(f.get(in1._2).asInstanceOf[String])) {
      log.info("Emitting main stream: " + (in1._1, in1._2, in1._3 += lastOtherStreamValues(f.get(in1._2).asInstanceOf[String])._3))
      val updated: scala.collection.mutable.ListBuffer[Boolean] = in1._3 :+ lastOtherStreamValues(f.get(in1._2).asInstanceOf[String])._3
      out.collect((in1._1, in1._2, updated))
    } else {
      log.info("Emitting main stream: " + in1)
      out.collect((in1))
    }
  }

  override def flatMap2(in2: (String, S), out: Collector[(String, T, scala.collection.mutable.ListBuffer[Boolean])]) = {

    if(lastOtherStreamValues.contains(in2._1)){

      log.info("Updating condition value for data subject " + in2._1)

      val userPastEventPolicy: PastCondition = lastOtherStreamValues(in2._1)._2

      log.info("Evaluating condition: " + userPastEventPolicy)

      val a: Field = in2._2.getClass.getDeclaredField(userPastEventPolicy.attributeName)
      a.setAccessible(true)

      val t: Field = in2._2.getClass.getDeclaredField("eventTime")
      t.setAccessible(true)

      val updatedList: List[(String, S)] = (lastOtherStreamValues(in2._1)._1 :+ in2).sortBy((e: (String, S)) => t.get(e._2).asInstanceOf[Long]).dropWhile((e: (String, S)) => t.get(in2._2).asInstanceOf[Long] - t.get(e._2).asInstanceOf[Long] > userPastEventPolicy.timeWindowMilliseconds)

      log.info("Evaluating window of events: " + lastOtherStreamValues(in2._1)._1)

      userPastEventPolicy.operator match {
        case Operator.EQUAL => lastOtherStreamValues.put(in2._1, (updatedList, userPastEventPolicy, updatedList.exists((e: (String, S)) => a.get(e._2).equals(userPastEventPolicy.value))))
        case Operator.NOT_EQUAL => lastOtherStreamValues.put(in2._1, (updatedList, userPastEventPolicy, !updatedList.exists((e: (String, S)) => a.get(e._2).equals(userPastEventPolicy.value))))
        case Operator.GRATER => lastOtherStreamValues.put(in2._1, (updatedList, userPastEventPolicy, updatedList.exists((e: (String, S)) => a.get(e._2).asInstanceOf[Int] > userPastEventPolicy.value.asInstanceOf[Int])))
        case Operator.GREATER_OR_EQUAL => lastOtherStreamValues.put(in2._1, (updatedList, userPastEventPolicy, updatedList.exists((e: (String, S)) => a.get(e._2).asInstanceOf[Int] >= userPastEventPolicy.value.asInstanceOf[Int])))
        case Operator.LESS => lastOtherStreamValues.put(in2._1, (updatedList, userPastEventPolicy, updatedList.exists((e: (String, S)) => a.get(e._2).asInstanceOf[Int] < userPastEventPolicy.value.asInstanceOf[Int])))
        case Operator.LESS_OR_EQUAL => lastOtherStreamValues.put(in2._1, (updatedList, userPastEventPolicy, updatedList.exists((e: (String, S)) => a.get(e._2).asInstanceOf[Int] <= userPastEventPolicy.value.asInstanceOf[Int])))
      }

      log.info("Updated condition value: " + lastOtherStreamValues(in2._1)._3)

    }
  }

}