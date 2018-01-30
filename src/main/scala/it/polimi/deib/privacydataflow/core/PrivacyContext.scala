package it.polimi.deib.privacydataflow.core

import java.util.Date

/**
  * Created by miik on 13/08/17.
  */
class PrivacyContext(var purpose: String, var serviceUser: String, var timestamp: Long) extends Serializable {

  def this() {
    this(null, null, -1)
  }

  override def toString: String = {
    val sb = new StringBuilder
    val d = new Date(timestamp)

    sb.append("@" + timestamp)
    sb.append(" context (")
    sb.append(serviceUser).append(")")

    sb.toString
  }

}