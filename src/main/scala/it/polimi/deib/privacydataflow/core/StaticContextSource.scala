package it.polimi.deib.privacydataflow.core

import org.apache.flink.streaming.api.functions.source.SourceFunction
import scala.collection.mutable.Map
/**
  * Created by miik on 16/10/17.
  */
class StaticContextSource(initialDelay: Int, minIntervalBetweenContextSwitch: Int, maxIntervalBetweenContextSwitch: Int, nSwitch: Int, sleepTimeBeforeFinish: Int) extends SourceFunction[Map[String, String]]  {

  private var staticContextVars: Map[String, String] = _

  override def cancel(): Unit = ???

  def setVariable(varName: String): Unit = {
    staticContextVars.put(varName, null)
  }

  override def run(sourceContext: SourceFunction.SourceContext[Map[String, String]]): Unit = ???
}
