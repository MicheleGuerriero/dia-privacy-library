package it.polimi.deib.privacydataflow.core

/**
  * Created by miik on 12/08/17.
  */
class ViewGeneralizationPolicy(var dataSubject: String) {

  private var pastConditions: List[PastCondition] = _

  def this() {
    this(null)
  }
}