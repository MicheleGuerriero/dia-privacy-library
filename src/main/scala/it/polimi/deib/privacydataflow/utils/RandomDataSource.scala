package it.polimi.deib.privacydataflow.utils

import it.polimi.deib.privacydataflow.example1.FinancialTransaction
import org.apache.flink.streaming.api.functions.source.SourceFunction

/**
  * Created by miik on 17/09/17.
  */
class RandomDataSource(initialDelay: Int, nDataSubject: Int, minIntervalBetweenEmit: Int, maxIntervalBetweenEmit: Int, isNanoseconds: Boolean, minNumber: Int, maxNumber: Int, nTuples: Int, sleepTimeBeforeFinish: Int) extends SourceFunction[RandomData]  {

  override def cancel(): Unit = {

  }

  override def run(sourceContext: SourceFunction.SourceContext[RandomData]): Unit = {
    val randomSleep = scala.util.Random
    val randomAmount = scala.util.Random
    val randomDataSubject = scala.util.Random

    var tCounter = 0

    Thread.sleep(initialDelay)

    while(tCounter < nTuples){

      //emit tuple
      val t: RandomData = new RandomData("ds" + (randomDataSubject.nextInt(nDataSubject) + 1), minNumber + randomAmount.nextInt(maxNumber - minNumber), getEventTime())

      sourceContext.collect(t)

      tCounter += 1

      // sleep random time
      if (isNanoseconds) {
        //Thread.sleep(0, minIntervalBetweenTransactions + randomSleep.nextInt((maxIntervalBetweenTransactions - minIntervalBetweenTransactions) + 1))
        val INTERVAL: Long = minIntervalBetweenEmit + randomSleep.nextInt((maxIntervalBetweenEmit - minIntervalBetweenEmit))
        val start: Long = System.nanoTime
        var end: Long = 0
        do {
          end = System.nanoTime
        } while (start + INTERVAL >= end)
      } else {
        Thread.sleep(minIntervalBetweenEmit + randomSleep.nextInt((maxIntervalBetweenEmit - minIntervalBetweenEmit) + 1))
      }
    }

    Thread.sleep(sleepTimeBeforeFinish)

  }


  def getEventTime(): Long = {
    System.currentTimeMillis()
  }
}
