package it.polimi.deib.privacydataflow.core

import java.io.PrintStream
import java.net.{InetAddress, Socket}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction
import org.apache.flink.util.Collector

/**
  * Created by miik on 12/08/17.
  */
class ViewBuilder[T](timestampServerIp: String, timestampServerPort: Int) extends RichCoFlatMapFunction[(String, T, scala.collection.mutable.ListBuffer[Boolean]), PrivacyContext, (String, T, scala.collection.mutable.ListBuffer[Boolean])] {

  private val contextualPatterns: scala.collection.mutable.Map[String, scala.collection.mutable.ListBuffer[(PrivacyContext, List[ContextCondition])]] = scala.collection.mutable.Map[String, scala.collection.mutable.ListBuffer[(PrivacyContext, List[ContextCondition])]]()

  private var generalizationVectors: scala.collection.mutable.Map[String, GeneralizationVector] = scala.collection.mutable.Map[String, GeneralizationVector]()

  private var generalizationFunctions: scala.collection.mutable.Map[String, scala.collection.mutable.Map[(String, Integer), Object => Object]] =  scala.collection.mutable.Map[String,  scala.collection.mutable.Map[(String, Integer), Object => Object]]()

  private var context: PrivacyContext = _

  var socketWriter: PrintStream = _

  override def open(parameters: Configuration): Unit = {
    socketWriter = new PrintStream(new Socket(InetAddress.getByName(timestampServerIp), timestampServerPort).getOutputStream())
  }


  def addGeneralizationFunction(dataSubject: String, attribute: String, level: Integer, f: Object => Object): Unit = {
    if(this.generalizationFunctions.contains(dataSubject))
      this.generalizationFunctions(dataSubject).put((attribute, level), f)
    else
      this.generalizationFunctions += dataSubject -> scala.collection.mutable.Map((attribute, level) -> f)
  }

  def addGeneralizationVector(dataSubject: String, gv: GeneralizationVector): Unit = {
      this.generalizationVectors += dataSubject -> gv
  }

  def addContextualPattern(dataSubject: String, cp: PrivacyContext, conds: List[ContextCondition]): Unit = {
    if(this.contextualPatterns.contains(dataSubject))
      this.contextualPatterns(dataSubject) += ((cp, conds))
    else
      this.contextualPatterns.put(dataSubject, scala.collection.mutable.ListBuffer((cp, conds)))
  }

  private def matchContext(dataSubject: String, current: T): Boolean = {

    var toReturn: Boolean = false
    if(contextualPatterns.contains(dataSubject) && context != null){
      val contexts: scala.collection.mutable.ListBuffer[(PrivacyContext, List[ContextCondition])] = contextualPatterns(dataSubject)
      for(toMatch <- contexts) {
        if (toMatch._1.purpose != null) {
          if (toMatch._1.purpose.equals(context.purpose)) {
            if (toMatch._1.serviceUser != null) {
              if (toMatch._1.serviceUser.equals(context.serviceUser)) {
                var r = true
                for(c <- toMatch._2){
                  val field = current.getClass.getDeclaredField(c.attributeName)
                  field.setAccessible(true)
                  if(field.get(current) != null){
                    c.operator match {
                      case Operator.EQUAL => r = r && field.get(current).equals(c.value)
                      case Operator.NOT_EQUAL => r = r && !field.get(current).equals(c.value)
                      case Operator.GRATER => r = r && field.get(current).asInstanceOf[Int] > c.value.asInstanceOf[Int]
                      case Operator.GREATER_OR_EQUAL => r = r && field.get(current).asInstanceOf[Int] >= c.value.asInstanceOf[Int]
                      case Operator.LESS => r = r && field.get(current).asInstanceOf[Int] < c.value.asInstanceOf[Int]
                      case Operator.LESS_OR_EQUAL => r = r && field.get(current).asInstanceOf[Int] <= c.value.asInstanceOf[Int]
                    }
                  } else{
                    r = false
                  }
                }
                if(r){
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

  override def flatMap1(in1: (String, T, scala.collection.mutable.ListBuffer[Boolean]), collector: Collector[(String, T, scala.collection.mutable.ListBuffer[Boolean])]): Unit = {

    if(this.generalizationVectors.contains(in1._1)){
      if(in1._3.isEmpty){
        if(matchContext(in1._1, in1._2)){
          val gv = this.generalizationVectors(in1._1)
          for(a: String <- gv.vector.keys){
            val field = in1._2.getClass.getDeclaredField(a)
            field.setAccessible(true)
            var finalVal = field.get(in1._2)
            for(i <- 1 to gv.vector(a)){
              finalVal = this.generalizationFunctions(in1._1)(a, i)(finalVal)
            }
            field.set(in1._2, finalVal)
          }
        }
      } else {
        if(this.contextualPatterns.contains(in1._1)){
          if(in1._3.fold(true)(_ && _) && matchContext(in1._1, in1._2)){
            val gv = this.generalizationVectors(in1._1)
            for(a: String <- gv.vector.keys){
              val field = in1._2.getClass.getDeclaredField(a)
              field.setAccessible(true)
              var finalVal = field.get(in1._2)
              for(i <- 1 to gv.vector(a)){
                finalVal = this.generalizationFunctions(in1._1)(a, i)(finalVal)
              }
              field.set(in1._2, finalVal)
            }
          }
        } else {
          if(in1._3.fold(true)(_ && _)){
            val gv = this.generalizationVectors(in1._1)
            for(a: String <- gv.vector.keys){
              val field = in1._2.getClass.getDeclaredField(a)
              field.setAccessible(true)
              var finalVal = field.get(in1._2)
              for(i <- 1 to gv.vector(a)){
                finalVal = this.generalizationFunctions(in1._1)(a, i)(finalVal)
              }
              field.set(in1._2, finalVal)
            }
          }
        }
      }
    }

    val tId = in1._2.getClass.getDeclaredField("transactionId")
    tId.setAccessible(true)
    collector.collect(in1)
    socketWriter.println(tId.get(in1._2) + "_end")

  }

  override def flatMap2(in2: PrivacyContext, collector: Collector[(String, T, scala.collection.mutable.ListBuffer[Boolean])]): Unit = {
    System.out.println("###########UPDATING CONTEXT: " + in2 + " " + System.out.println(System.currentTimeMillis()))
    this.context = in2
  }
}
