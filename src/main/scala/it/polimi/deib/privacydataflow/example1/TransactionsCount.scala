package it.polimi.deib.privacydataflow.example1

import java.util.Date

/**
  * Created by miik on 13/08/17.
  */
class TransactionsCount(var dataSubject: String, var tCount: Integer, var eventTime: Long) extends Serializable {

  def this() {
    this(null, -1, -1)
  }

  override def toString: String = {
    val sb = new StringBuilder

    val d = new Date(eventTime)
    sb.append("@" + eventTime)
    sb.append(" emit (")
    sb.append("count").append(",")
    sb.append(dataSubject).append(",")
    sb.append(tCount).append(")")

    sb.toString
  }

}
