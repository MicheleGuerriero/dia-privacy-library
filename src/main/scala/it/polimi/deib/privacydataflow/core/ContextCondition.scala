package it.polimi.deib.privacydataflow.core

/**
  * Created by miik on 03/10/17.
  */
class ContextCondition(var attributeName: String, var attributeType: String, var operator: Operator, var value: Object) extends Serializable  {
  def this() {
    this(null, null, null, null)
  }

  override def toString: String = {
    val sb = new StringBuilder
    sb.append(attributeName).append(",")
    sb.append(attributeType).append(",")
    sb.append(operator).append(",")
    sb.append(value)

    sb.toString
  }
}
