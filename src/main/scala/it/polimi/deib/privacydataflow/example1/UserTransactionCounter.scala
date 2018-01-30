package it.polimi.deib.privacydataflow.example1

import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
  * Created by micheleguerriero on 10/07/17.
  */
class UserTransactionCounter extends WindowFunction [FinancialTransaction, TransactionsCount, String, TimeWindow]{

  override def apply(key: String, window: TimeWindow, input: Iterable[FinancialTransaction], out: Collector[TransactionsCount]): Unit = {

    //val lastInWindowsId: String  = input.toList.sortBy(_.eventTime).take(1)(1).transactionId

    out.collect(new TransactionsCount(key, new Integer(input.size), window.getEnd))

    //val endTime: Long  = System.nanoTime()
    //input.foreach((t: FinancialTransaction) => System.out.println(endTime + " EndTransaction " + t.transactionId))

  }

}
