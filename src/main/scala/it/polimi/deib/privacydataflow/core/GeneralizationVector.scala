package it.polimi.deib.privacydataflow.core

/**
  * Created by miik on 12/08/17.
  */
class GeneralizationVector(var vector: scala.collection.mutable.Map[String, Integer]) extends Serializable {
  def this() {
    this(null)
  }
}
