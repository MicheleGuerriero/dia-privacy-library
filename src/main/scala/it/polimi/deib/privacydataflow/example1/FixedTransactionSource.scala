package it.polimi.deib.privacydataflow.example1

import java.io.PrintStream
import java.net.{InetAddress, Socket}

import it.polimi.deib.privacydataflow.core.PrivacyContext
import org.apache.flink.streaming.api.functions.source.SourceFunction

/**
  * Created by miik on 16/08/17.
  */
class FixedTransactionSource(var initialDelay: Int, timestampServerIp: String, timestampServerPort: Int) extends SourceFunction[FinancialTransaction] {


  override def run(sourceContext: SourceFunction.SourceContext[FinancialTransaction]): Unit = {

    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    // USED FOR TESTING THE DSE POLICIES
    /*    val transactions: scala.collection.immutable.ListMap[FinancialTransaction, Long] = scala.collection.immutable.ListMap(
          new FinancialTransaction("t1", "u4", 1500, format.parse("2010-10-10 10:10:02").getTime) -> 2000,
          new FinancialTransaction("t2", "u4", 1100, format.parse("2010-10-10 10:10:04").getTime) -> 1000,
          new FinancialTransaction("t3", "u1", 900, format.parse("2010-10-10 10:10:05").getTime) -> 2000,
          new FinancialTransaction("t4", "u1", 2000, format.parse("2010-10-10 10:10:07").getTime) -> 1000,
          new FinancialTransaction("t5", "u1", 350, format.parse("2010-10-10 10:10:08").getTime) -> 3000,
          new FinancialTransaction("t6", "u4", 350, format.parse("2010-10-10 10:10:11").getTime) -> 2000

        )*/

    // USED FOR TESTING THE VG POLICIES
    val transactions: scala.collection.immutable.ListMap[FinancialTransaction, Long] = scala.collection.immutable.ListMap(
      new FinancialTransaction("t1", "u1", 1500, format.parse("2010-10-10 10:10:02").getTime) -> 1000,
      new FinancialTransaction("t2", "u1", 200, format.parse("2010-10-10 10:10:03").getTime) -> 1000,
      new FinancialTransaction("t3", "u1", 700, format.parse("2010-10-10 10:10:04").getTime) -> 2000,
      new FinancialTransaction("t4", "u1", 900, format.parse("2010-10-10 10:10:06").getTime) -> 2000,
      new FinancialTransaction("t5", "u1", 2000, format.parse("2010-10-10 10:10:08").getTime) -> 3000,
      new FinancialTransaction("t6", "u1", 350, format.parse("2010-10-10 10:10:11").getTime) -> 2000

    )

    val s = new Socket(InetAddress.getByName(timestampServerIp), timestampServerPort)
    val socketWriter = new PrintStream(s.getOutputStream())


    Thread.sleep(initialDelay)

    for (t <- transactions) {

      sourceContext.collect(t._1)
      socketWriter.println(t._1.transactionId + "_start")

      Thread.sleep(transactions(t._1))

    }

    s.close()

  }

  override def cancel(): Unit = {

  }
}

