package it.polimi.deib.privacydataflow.example1

import java.io.PrintStream
import java.net.{InetAddress, Socket}
import java.util.concurrent.TimeUnit

import org.apache.flink.streaming.api.functions.source.SourceFunction

/**
  * Created by micheleguerriero on 12/07/17.
  */
class TransactionSource(initialDelay: Int, nDataSubject: Int, minIntervalBetweenTransactions: Int, maxIntervalBetweenTransactions: Int, isNanoseconds: Boolean, minAmountPerTransaction: Int, maxAmountPerTransaction: Int, nTransaction: Int, sleepTimeBeforeFinish: Int, timestampServerIp: String, timestampServerPort: Int) extends SourceFunction[FinancialTransaction] {

  private final val WARM_UP_TUPLES: Integer = 50000
  private final val COOL_DOWN_TUPLES: Integer = 50000

  private var started: Boolean = false
  private var coolingDown: Boolean = false

  override def cancel(): Unit = {

  }

  override def run(sourceContext: SourceFunction.SourceContext[FinancialTransaction]): Unit = {
    val randomSleep = scala.util.Random
    val randomAmount = scala.util.Random
    val randomDataSubject = scala.util.Random

    var tCounter = 0

    val s = new Socket(InetAddress.getByName(timestampServerIp), timestampServerPort)
    val socketWriter = new PrintStream(s.getOutputStream())


    Thread.sleep(initialDelay)

    while (tCounter < nTransaction) {

      if (tCounter == WARM_UP_TUPLES) {
        socketWriter.println("jobStart")
        this.started = true
      }

      if (tCounter == nTransaction - COOL_DOWN_TUPLES) {
        this.coolingDown = true
      }

      //create new transaction with random amount

      val t: FinancialTransaction = new FinancialTransaction("t" + tCounter, "ds" + (randomDataSubject.nextInt(nDataSubject) + 1), minAmountPerTransaction + randomAmount.nextInt(maxAmountPerTransaction - minAmountPerTransaction), getEventTime())

      //emit tuple
      if (started & !coolingDown)
        socketWriter.println("t" + tCounter + "_start")

      sourceContext.collect(t)
      tCounter += 1

      // sleep random time
      if (isNanoseconds) {
        //Thread.sleep(0, minIntervalBetweenTransactions + randomSleep.nextInt((maxIntervalBetweenTransactions - minIntervalBetweenTransactions) + 1))
        val INTERVAL: Long = minIntervalBetweenTransactions + randomSleep.nextInt((maxIntervalBetweenTransactions - minIntervalBetweenTransactions))
        val start: Long = System.nanoTime
        var end: Long = 0
        do {
          end = System.nanoTime
        } while (start + INTERVAL >= end)
      } else {
        val INTERVAL: Long = (minIntervalBetweenTransactions + randomSleep.nextInt((maxIntervalBetweenTransactions - minIntervalBetweenTransactions)))* 1000000
        val start: Long = System.nanoTime
        var end: Long = 0
        do {
          end = System.nanoTime
        } while (start + INTERVAL >= end)
      }

    }

    Thread.sleep(sleepTimeBeforeFinish)
    s.close()

  }

  def getEventTime(): Long = {
    System.currentTimeMillis()
  }
}
