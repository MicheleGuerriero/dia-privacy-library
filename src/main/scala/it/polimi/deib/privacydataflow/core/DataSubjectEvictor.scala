package it.polimi.deib.privacydataflow.core

import java.io.PrintStream
import java.net.{InetAddress, Socket}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by miik on 12/08/17.
  */
class DataSubjectEvictor[T](timestampServerIp: String, timestampServerPort: Int) extends RichCoFlatMapFunction[(String, T, scala.collection.mutable.ListBuffer[Boolean]), PrivacyContext, T] {

  val log: Logger = LoggerFactory.getLogger(classOf[DataSubjectEvictor[T]])
  private val contextualPatterns: scala.collection.mutable.Map[String, scala.collection.mutable.ListBuffer[(PrivacyContext, List[ContextCondition])]] = scala.collection.mutable.Map[String, scala.collection.mutable.ListBuffer[(PrivacyContext, List[ContextCondition])]]()

  private var context: PrivacyContext = _

  var socketWriter: PrintStream = _

  override def open(parameters: Configuration): Unit = {
    socketWriter = new PrintStream(new Socket(InetAddress.getByName(timestampServerIp), timestampServerPort).getOutputStream())
  }

  def addContextualPattern(dataSubject: String, cp: PrivacyContext, conds: List[ContextCondition]): Unit = {
    if (this.contextualPatterns.contains(dataSubject))
      this.contextualPatterns(dataSubject) += ((cp, conds))
    else
      this.contextualPatterns.put(dataSubject, scala.collection.mutable.ListBuffer((cp, conds)))
  }

  private def matchContext(dataSubject: String, current: T): Boolean = {

    var toReturn: Boolean = false
    if (contextualPatterns.contains(dataSubject) && context != null) {
      val contexts: scala.collection.mutable.ListBuffer[(PrivacyContext, List[ContextCondition])] = contextualPatterns(dataSubject)
      for (toMatch <- contexts) {
        if (toMatch._1.purpose != null) {
          if (toMatch._1.purpose.equals(context.purpose)) {
            if (toMatch._1.serviceUser != null) {
              if (toMatch._1.serviceUser.equals(context.serviceUser)) {
                var r = true
                for (c <- toMatch._2) {
                  val field = current.getClass.getDeclaredField(c.attributeName)
                  field.setAccessible(true)
                  if (field.get(current) != null) {
                    c.operator match {
                      case Operator.EQUAL => r = r && field.get(current).equals(c.value)
                      case Operator.NOT_EQUAL => r = r && !field.get(current).equals(c.value)
                      case Operator.GRATER => r = r && field.get(current).asInstanceOf[Int] > c.value.asInstanceOf[Int]
                      case Operator.GREATER_OR_EQUAL => r = r && field.get(current).asInstanceOf[Int] >= c.value.asInstanceOf[Int]
                      case Operator.LESS => r = r && field.get(current).asInstanceOf[Int] < c.value.asInstanceOf[Int]
                      case Operator.LESS_OR_EQUAL => r = r && field.get(current).asInstanceOf[Int] <= c.value.asInstanceOf[Int]
                    }
                  } else {
                    r = false
                  }
                }
                if (r) {
                  toReturn = true
                }
              }
            }
          }
        }
      }
    }

    toReturn
  }

  override def flatMap1(in1: (String, T, scala.collection.mutable.ListBuffer[Boolean]), collector: Collector[T]): Unit = {
    log.info("Received new input tuple: " + in1._2)

    if (in1._3.isEmpty) {
      log.info("Evaluating policy with no past conditions.")
      if (!matchContext(in1._1, in1._2)) {
        log.info("No matching context, emitting the tuple.")
        collector.collect(in1._2)
      } else {
        log.info("Matching context found, avoiding tuple emissions.")
      }
    } else {
      if (this.contextualPatterns.contains(in1._1)) {
        log.info("Evaluating policy with past conditions and with context:" + in1._3)
        if (!in1._3.fold(true)(_ && _) || !matchContext(in1._1, in1._2)) {
          log.info("No matching context or false past condition, emitting the tuple.")
          collector.collect(in1._2)
        } else {
          log.info("Matching context found and all past conditions true, avoiding tuple emission.")
        }
      } else {
        log.info("Evaluating policy with only past conditions and no context:" + in1._3)
        if (!in1._3.fold(true)(_ && _)) {
          log.info("False past condition found, emitting the tuple:")
          collector.collect(in1._2)
        } else {
          log.info("All past conditions true, avoiding tuple emission.")
        }
      }
    }

    val tId = in1._2.getClass.getDeclaredField("transactionId")
    tId.setAccessible(true)
    socketWriter.println(tId.get(in1._2) + "_end")

  }

  override def flatMap2(in2: PrivacyContext, collector: Collector[T]): Unit = {
    this.context = in2

    log.info("Updated context: " + in2)
  }
}
