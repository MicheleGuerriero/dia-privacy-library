package it.polimi.deib.privacydataflow.example1

import java.io.{FileInputStream, PrintStream}
import java.net.{InetAddress, Socket}
import java.util.Properties
import java.util.concurrent.TimeUnit

import it.polimi.deib.privacydataflow._
import it.polimi.deib.privacydataflow.core._
import it.polimi.deib.privacydataflow.utils.{RandomData, RandomDataSource}
import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time

import scala.collection.mutable.Map

/**
  * Created by micheleguerriero on 10/07/17.
  */
object DataSubjectDerivedFinancialTransactionsTestJob {

  def main(args: Array[String]) {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val (
      timestampServerIp,
      pathToResultFolder,
      timestampServerPort,
      privacyOn,
      topologyParallelism,
      minIntervalBetweenTransactions,
      maxIntervalBetweenTransactions,
      nTransactions,
      nDataSubject,
      minIntervalBetweenContextSwitch,
      maxIntervalBetweenContextSwitch,
      nContextSwitch,
      isNanoSeconds,
      bufferTimeout,
      nPastCond) =
      try {
        val prop = new Properties()
        prop.load(new FileInputStream("config.properties"))

        (
          prop.getProperty("timestampServerIp"),
          prop.getProperty("pathToResultFolder"),
          new Integer(prop.getProperty("timestampServerPort")),
          prop.getProperty("privacyOn").toBoolean,
          new Integer(prop.getProperty("topologyParallelism")),
          new Integer(prop.getProperty("minIntervalBetweenTransactions")),
          new Integer(prop.getProperty("maxIntervalBetweenTransactions")),
          new Integer(prop.getProperty("nTransactions")),
          new Integer(prop.getProperty("nDataSubject")),
          new Integer(prop.getProperty("minIntervalBetweenContextSwitch")),
          new Integer(prop.getProperty("maxIntervalBetweenContextSwitch")),
          new Integer(prop.getProperty("nContextSwitch")),
          prop.getProperty("isNanoSeconds").toBoolean,
          new Integer(prop.getProperty("bufferTimeout")).toLong,
          new Integer(prop.getProperty("nPastCond"))
        )
      } catch { case e: Exception =>
        e.printStackTrace()
        sys.exit(1)
      }

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setBufferTimeout(bufferTimeout)
    var executionConfig = env.getConfig


    var inputStream = env.addSource(
      new TransactionSource(
        1000,
        nDataSubject,
        minIntervalBetweenTransactions,
        maxIntervalBetweenTransactions,
        isNanoSeconds,
        300,
        800,
        nTransactions,
        0,
        timestampServerIp,
        timestampServerPort
      )
    )
      .name("input-source")
      .assignAscendingTimestamps(_.eventTime)
      .setParallelism(1)
      .name("input-timestamp-assigner")

    val contextStream = env.addSource(new PrivateContextSource(50, minIntervalBetweenContextSwitch, maxIntervalBetweenContextSwitch, nContextSwitch, 0))
      .name("context-source")
      .assignAscendingTimestamps(_.timestamp)
      .setParallelism(1)
      .name("context-timestamp-assigner")



    /*    var inputStream = env.addSource(new FixedTransactionSource(1000, "localhost", 4444))
          .assignAscendingTimestamps(_.eventTime)
          .setParallelism(1)
          .name("input-source")

        val contextStream = env.addSource(new FixedPrivateContextSource(0))
          .assignAscendingTimestamps(_.timestamp)
          .name("context-source")
          .setParallelism(1)*/

    var agentTotalAmount: DataStream[TotalAmount] = inputStream
      .keyBy(_.agent)
      .timeWindow(Time.milliseconds(3))
      .apply(new UserTotalAmountCalculator(timestampServerIp, timestampServerPort, !privacyOn))
      .setParallelism(topologyParallelism)
      .name("amount-counter")
      .assignAscendingTimestamps(_.eventTime)
      .setParallelism(topologyParallelism)
      .name("amount-counter-timestamp-assigner")

    var agentNumberOfTransaction: DataStream[TransactionsCount] = inputStream
      .keyBy(_.agent)
      .timeWindow(Time.milliseconds(3))
      .apply(new UserTransactionCounter)
      .setParallelism(topologyParallelism)
      .name("transaction-counter")
      .assignAscendingTimestamps(_.eventTime)
      .setParallelism(topologyParallelism)
      .name("transaction-counter-timestamp-assigner")

    if (privacyOn) {

      val privateTopConsumers: DataSubjectDerivedPrivateStream[TopConsumers, TotalAmount] = new DataSubjectDerivedPrivateStream[TopConsumers, TotalAmount](timestampServerIp, timestampServerPort, topologyParallelism)
      //privateTopConsumers.setStreamToProtect(topConsumers, agentTotalAmount, new TopConsumersCounter("localhost", 4444), Time.seconds(9))
      privateTopConsumers.setStreamToProtect(agentTotalAmount, new TopConsumersCounter(), Time.seconds(6))

      privateTopConsumers.addContextualPattern("u1", new PrivacyContext("research", "u3", -1), List())
      privateTopConsumers.addDataSubjectSpecificStreamPastConditions(agentNumberOfTransaction, Map("u1" -> new PastCondition("tCount", "Integer", Operator.GREATER_OR_EQUAL, new Integer(25), 20)))

      if(nPastCond>0) {
        val randomNumberStream1 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream1, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>1) {
        val randomNumberStream2 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream2, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>2) {
        val randomNumberStream3 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream3, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>3) {
        val randomNumberStream4 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream4, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>4) {
        val randomNumberStream5 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream5, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>5) {
        val randomNumberStream6 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream6, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>6) {
        val randomNumberStream7 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream7, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>7) {
        val randomNumberStream8 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream8, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }

      if(nPastCond>8) {
        val randomNumberStream9 = env.addSource(
          new RandomDataSource(
            0,
            nDataSubject,
            1,
            3,
            false,
            9,
            100,
            nTransactions / 1000,
            0
          )
        )

        privateTopConsumers.addDataSubjectSpecificStreamPastConditions(randomNumberStream9, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))
      }
      privateTopConsumers.addContextualPattern("u4", new PrivacyContext("marketing", "u2", -1), List())
      privateTopConsumers.addContextualPattern("u4", new PrivacyContext("marketing", "u3", -1), List())

      //privateTopConsumers.addDataSubjectSpecificStreamPastConditions(agentNumberOfTransaction, Map("u4" -> new PastCondition("tCount", "Integer", Operator.GREATER_OR_EQUAL, new Integer(4), 2000)))
      //privateTopConsumers.addDataSubjectSpecificStreamPastConditions(agentTotalAmount, Map("u4" -> new PastCondition("amount", "Integer", Operator.GRATER, new Integer(1000), 3000)))

      val topConsumers = privateTopConsumers.finilize(env, contextStream)

      topConsumers
        .writeAsText(pathToResultFolder + "/topConsumers.txt")
        .setParallelism(1)
    } else {

      val topConsumers: DataStream[TopConsumers] = agentTotalAmount
        .timeWindowAll(Time.seconds(6))
        .apply(new TopConsumersCounter())
        .setParallelism(1)
        .name("top-users-counter")
        .assignAscendingTimestamps(_.eventTime)

      topConsumers
        .writeAsText(pathToResultFolder + "/topConsumers.txt")
        .setParallelism(1)
    }
    /*    inputStream
          .writeAsText(args(2) + "/input.txt")
          .setParallelism(1)

        contextStream
          .writeAsText(args(2) + "/context.txt")
          .setParallelism(1)

        agentNumberOfTransaction
          .keyBy(_.dataSubject)
          .writeAsText(args(2) + "/tCount.txt")
          .setParallelism(1)

        agentTotalAmount
          .keyBy(_.dataSubject)
          .writeAsText(args(2) + "/amount.txt")
          .setParallelism(1)*/

    import java.io.PrintWriter
    try {
      val out = new PrintWriter(pathToResultFolder + "/plan.json")
      try
        out.print(env.getExecutionPlan)
      finally if (out != null) out.close()
    }

    val result: JobExecutionResult = env.execute()

    val s = new Socket(InetAddress.getByName(timestampServerIp), timestampServerPort)
    val socketWriter = new PrintStream(s.getOutputStream())
    socketWriter.println("jobEnd")
    s.close();

    try {
      val out = new PrintWriter(pathToResultFolder + "/throughput.txt")
      try {
        out.println(nTransactions.toDouble / result.getNetRuntime(TimeUnit.MILLISECONDS))
        //out.println(nTransactions.toDouble * 0.8 / result.getNetRuntime(TimeUnit.MILLISECONDS))
        //out.println((1/(nTransactions.toDouble/ result.getNetRuntime(TimeUnit.MILLISECONDS)*0.8) * 100000 + 1).toInt)
        //out.println((1/(nTransactions.toDouble/ result.getNetRuntime(TimeUnit.MILLISECONDS)*0.8) * 100000).toInt)
      }
      finally if (out != null) out.close()
    }

    System.out.print("######### THROUGHPUT: " + nTransactions.toDouble / result.getNetRuntime(TimeUnit.MILLISECONDS) + " ######### ")


  }
}
