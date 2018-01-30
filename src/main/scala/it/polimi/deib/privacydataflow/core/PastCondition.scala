package it.polimi.deib.privacydataflow.core

/**
  * Created by miik on 12/08/17.
  */
class PastCondition(var attributeName: String, var attributeType: String, var operator: Operator, var value: Object, var timeWindowMilliseconds: Long) extends Serializable {
  def this() {
    this(null, null, null, null, -1)
  }

  override def toString: String = {
    val sb = new StringBuilder
    sb.append(attributeName).append(",")
    sb.append(attributeType).append(",")
    sb.append(operator).append(",")
    sb.append(value).append(",")
    sb.append(timeWindowMilliseconds).append(",")

    sb.toString
  }
}
