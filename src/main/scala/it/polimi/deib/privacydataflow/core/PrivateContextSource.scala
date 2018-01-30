package it.polimi.deib.privacydataflow.core

import org.apache.flink.streaming.api.functions.source.SourceFunction

/**
  * Created by micheleguerriero on 12/07/17.
  */
class PrivateContextSource(initialDelay: Int, minIntervalBetweenContextSwitch: Int, maxIntervalBetweenContextSwitch: Int, nSwitch: Int, sleepTimeBeforeFinish: Int) extends SourceFunction[PrivacyContext] {

  private val purposes: List[String] = List("marketing")

  private val users: List[String] = List("u1")

  override def cancel(): Unit = {

  }

  override def run(sourceContext: SourceFunction.SourceContext[PrivacyContext]): Unit = {
    val randomSleep = scala.util.Random

    var cs = 0

    Thread.sleep(initialDelay)

    while (cs < nSwitch) {
      //create new transaction with random amount

      val user: String = scala.util.Random.shuffle(users).head
      val c: PrivacyContext = new PrivacyContext(scala.util.Random.shuffle(purposes).head, user, getEventTime())

      //emit tuple
      System.out.println("###########EMITTING CONTEXT: " + c + " " + System.out.println(System.currentTimeMillis()))
      sourceContext.collect(c)
      cs += 1

      // sleep random time
      Thread.sleep(minIntervalBetweenContextSwitch + randomSleep.nextInt((maxIntervalBetweenContextSwitch - minIntervalBetweenContextSwitch) + 1))

    }

    Thread.sleep(sleepTimeBeforeFinish)
  }


  def getEventTime(): Long = {
    System.currentTimeMillis()
  }

}
