package it.polimi.deib.privacydataflow.example1

import java.io.PrintStream
import java.net.{InetAddress, Socket}

import it.polimi.deib.privacydataflow.core.DataSubjectEvictor
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.function.{AllWindowFunction, RichAllWindowFunction}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

/**
  * Created by micheleguerriero on 10/07/17.
  */
class TopConsumersCounter extends RichAllWindowFunction[TotalAmount, TopConsumers, TimeWindow] {

  val log: Logger = LoggerFactory.getLogger(classOf[TopConsumersCounter])

  override def apply(window: TimeWindow, input: Iterable[TotalAmount], out: Collector[TopConsumers]): Unit = {

    log.info("Evaluating window:" + input)

    var cont = 0
    var lastTransactionIndex = -1
    var partialSums: mutable.Map[String,Int] = mutable.Map()

    for (t <- input) {
      if(partialSums.keySet.contains(t.dataSubject)){
        partialSums += (t.dataSubject -> (partialSums(t.dataSubject) + t.amount))
      } else{
        partialSums.put(t.dataSubject, t.amount)
      }

      if(t.transactionId.substring(1, t.transactionId.length).toInt > lastTransactionIndex){
        lastTransactionIndex = t.transactionId.substring(1, t.transactionId.length).toInt
      }
    }

    for (k <- partialSums){
      if(k._2 > 1000){
        cont = cont + 1
      }
    }
    
    out.collect(new TopConsumers(cont, window.getEnd))

  }
}