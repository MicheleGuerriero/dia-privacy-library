package it.polimi.deib.privacydataflow.example1

import java.io.PrintStream
import java.net.{InetAddress, Socket}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.function.{RichWindowFunction, WindowFunction}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
  * Created by micheleguerriero on 10/07/17.
  */


class UserTotalAmountCalculator(var timestampServerIp: String, var timestampServerPort: Int, var latencyTracking: Boolean) extends RichWindowFunction [FinancialTransaction, TotalAmount, String, TimeWindow]{

  var socketWriter: PrintStream = _

  override def open(parameters: Configuration): Unit = {
    socketWriter = new PrintStream(new Socket(InetAddress.getByName(timestampServerIp), timestampServerPort).getOutputStream())
  }

  override def apply(key: String, window: TimeWindow, input: Iterable[FinancialTransaction], out: Collector[TotalAmount]): Unit = {
    var sum = 0
    var lastTransactionIndex = -1

    for (t <- input) {
      sum += t.amount
      if(t.transactionId.substring(1, t.transactionId.length).toInt > lastTransactionIndex){
        lastTransactionIndex = t.transactionId.substring(1, t.transactionId.length).toInt
      }
    }

    out.collect(new TotalAmount("t" + lastTransactionIndex, key, new Integer(sum), window.getEnd))

    if(latencyTracking)
      socketWriter.println("t" + lastTransactionIndex + "_end")

  }

}

