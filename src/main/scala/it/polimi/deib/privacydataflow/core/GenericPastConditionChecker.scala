package it.polimi.deib.privacydataflow.core

import java.lang.reflect.Field

import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction
import org.apache.flink.util.Collector

import scala.collection.mutable.ListBuffer

/**
  * Created by miik on 14/08/17.
  */
class GenericPastConditionChecker[T,S] extends RichCoFlatMapFunction[(String, T, scala.collection.mutable.ListBuffer[Boolean]), S, (String, T, scala.collection.mutable.ListBuffer[Boolean])] {

  private var lastOtherStreamValues: scala.collection.mutable.Map[String, (PastCondition, Boolean)] = scala.collection.mutable.Map[String, (PastCondition, Boolean)]()

  private var lastEvents: scala.collection.mutable.ListBuffer[S] = ListBuffer[S]()

  def setPastConditions(conds: scala.collection.mutable.Map[String, PastCondition]): Unit = {
    for(c <- conds.keys){
      this.lastOtherStreamValues += c -> (conds(c), false)
    }
  }

  override def flatMap1(in1: (String, T, scala.collection.mutable.ListBuffer[Boolean]), out: Collector[(String, T, scala.collection.mutable.ListBuffer[Boolean])]) = {

    val f: Field = in1._2.getClass.getDeclaredField("dataSubject")
    f.setAccessible(true)
    if (lastOtherStreamValues.contains(f.get(in1._2).asInstanceOf[String])) {
      System.out.println("[PastConditionChecker] Emitting main stream: " + (in1._1, in1._2, in1._3 += lastOtherStreamValues(f.get(in1._2).asInstanceOf[String])._2))
      val updated: scala.collection.mutable.ListBuffer[Boolean] = in1._3 :+ lastOtherStreamValues(f.get(in1._2).asInstanceOf[String])._2
      out.collect((in1._1, in1._2, updated))
    } else {
      System.out.println("[PastConditionChecker] Emitting main stream: " + in1)
      out.collect((in1))
    }
  }

  override def flatMap2(in2: S, out: Collector[(String, T, scala.collection.mutable.ListBuffer[Boolean])]) = {

    var maxWindow: Long = -1
    for(k: String <- lastOtherStreamValues.keys){
      val tmp: Long = lastOtherStreamValues(k)._1.timeWindowMilliseconds
      if(tmp > maxWindow)
        maxWindow = tmp
    }

    val t: Field = in2.getClass.getDeclaredField("eventTime")
    t.setAccessible(true)

    lastEvents = (lastEvents :+ in2).sortBy((e: S) => t.get(e).asInstanceOf[Long]).dropWhile((e: S) => t.get(in2).asInstanceOf[Long] - t.get(e).asInstanceOf[Long] > maxWindow)

    //update condition value for each user
    for(k: String <- lastOtherStreamValues.keys){
      val pastCondition: PastCondition = lastOtherStreamValues(k)._1

      val updatedList: ListBuffer[S] = lastEvents.sortBy((e: S) => t.get(e).asInstanceOf[Long]).dropWhile((e: S) => t.get(in2).asInstanceOf[Long] - t.get(e).asInstanceOf[Long] > pastCondition.timeWindowMilliseconds)

      val a: Field = in2.getClass.getDeclaredField(pastCondition.attributeName)
      a.setAccessible(true)

      pastCondition.operator match {
        case Operator.EQUAL => lastOtherStreamValues.put(k, (pastCondition, updatedList.exists((e: S) => a.get(e).equals(pastCondition.value))))
        case Operator.NOT_EQUAL => lastOtherStreamValues.put(k, (pastCondition, !updatedList.exists((e: S) => a.get(e).equals(pastCondition.value))))
        case Operator.GRATER => lastOtherStreamValues.put(k, (pastCondition, !updatedList.exists((e: S) => a.get(e).asInstanceOf[Int] > pastCondition.value.asInstanceOf[Int])))
        case Operator.GREATER_OR_EQUAL => lastOtherStreamValues.put(k, (pastCondition, !updatedList.exists((e: S) => a.get(e).asInstanceOf[Int] >= pastCondition.value.asInstanceOf[Int])))
        case Operator.LESS => lastOtherStreamValues.put(k, (pastCondition, !updatedList.exists((e: S) => a.get(e).asInstanceOf[Int] < pastCondition.value.asInstanceOf[Int])))
        case Operator.LESS_OR_EQUAL => lastOtherStreamValues.put(k, (pastCondition, !updatedList.exists((e: S) => a.get(e).asInstanceOf[Int] <= pastCondition.value.asInstanceOf[Int])))
      }

    }
  }
}
