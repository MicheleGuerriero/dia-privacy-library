package it.polimi.deib.privacydataflow.example1

import java.util.Date

/**
  * Created by miik on 16/08/17.
  */
class TopConsumers(var nTopConsumers: Integer, var eventTime: Long) extends Serializable {
  def this() {
    this(-1, -1)
  }

  override def toString: String = {
    val sb = new StringBuilder
    sb.append(nTopConsumers).append(",")

    val d = new Date(eventTime)
    sb.append(d.toString)
    sb.toString
  }
}
