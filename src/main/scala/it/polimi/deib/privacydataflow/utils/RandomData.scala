package it.polimi.deib.privacydataflow.utils

import java.util.Date

/**
  * Created by miik on 03/10/17.
  */
class RandomData(var dataSubject: String, var data: Integer, var eventTime: Long) {
  def this() {
    this(null, -1, -1)
  }

  override def toString: String = {
    val sb = new StringBuilder

    sb.append("@" + eventTime)
    sb.append(" emit (")
    sb.append("rs").append(",")
    sb.append(dataSubject).append(",")
    sb.append(data).append(")")

    sb.toString


  }
}
