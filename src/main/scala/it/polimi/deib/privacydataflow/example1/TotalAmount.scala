package it.polimi.deib.privacydataflow.example1

import java.util.Date

/**
  * Created by miik on 13/08/17.
  */
class TotalAmount(var transactionId: String, var dataSubject: String, var amount: Integer, var eventTime: Long) extends Serializable {
  def this() {
    this(null, null, -1, -1)
  }

  override def toString: String = {
    val sb = new StringBuilder
    val d = new Date(eventTime)

    sb.append("@" + eventTime)
    sb.append(" publish (")
    sb.append("tot").append(",")
    sb.append(dataSubject).append(",")
    sb.append(amount).append(")")

    sb.toString


  }
}
