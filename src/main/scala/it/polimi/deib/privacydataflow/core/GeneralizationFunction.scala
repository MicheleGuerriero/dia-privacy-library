package it.polimi.deib.privacydataflow.core

/**
  * Created by miik on 12/08/17.
  */

class GeneralizationFunction extends Serializable {
  def generalize(value: Object, level: Int): Object = {
    var cont = 1
    var result = value
    while (cont <= level) {
      if(cont == 1){
        // assignment to be specified by the user
        result = null
      }
      cont = cont + 1
    }

    result
  }
}
