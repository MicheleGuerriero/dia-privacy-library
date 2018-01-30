package it.polimi.deib.privacydataflow.example1

import java.util.Date

/**
  * Created by micheleguerriero on 10/07/17.
  */
class FinancialTransaction(var transactionId: String, var agent: String, var amount: Int, var eventTime: Long) {
  def this() {
    this(null, null, -1, -1)
  }

  override def toString: String = {
    val sb = new StringBuilder
    sb.append(transactionId).append(",")
    sb.append(agent).append(",")
    sb.append(amount).append(",")
    val d = new Date(eventTime)
    sb.append(d.toString).append(",")
    sb.append(eventTime)
    sb.toString
  }
}

