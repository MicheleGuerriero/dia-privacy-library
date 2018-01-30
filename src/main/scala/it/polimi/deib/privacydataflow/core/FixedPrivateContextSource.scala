package it.polimi.deib.privacydataflow.core

import org.apache.flink.streaming.api.functions.source.SourceFunction

/**
  * Created by micheleguerriero on 12/07/17.
  */
class FixedPrivateContextSource(var initialDelay: Int) extends SourceFunction[PrivacyContext] {

  override def run(sourceContext: SourceFunction.SourceContext[PrivacyContext]): Unit = {
    val randomSleep = scala.util.Random

    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val contexts: Map[PrivacyContext, Long] = Map(
      new PrivacyContext("marketing", "u2", format.parse("2010-10-10 10:10:01").getTime) -> 4000,
      new PrivacyContext("research", "u3", format.parse("2010-10-10 10:10:05").getTime) -> 2000
    )

    Thread.sleep(initialDelay)

    for(c <- contexts) {

      sourceContext.collect(c._1)

      Thread.sleep(contexts(c._1))

    }
  }

  override def cancel(): Unit = {

  }
}
