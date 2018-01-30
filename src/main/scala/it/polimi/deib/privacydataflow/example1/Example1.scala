package it.polimi.deib.privacydataflow.example1

import java.io.{FileInputStream, PrintStream}
import java.net.{InetAddress, Socket}
import java.util.Properties
import java.util.concurrent.TimeUnit

import it.polimi.deib.privacydataflow.core._
import it.polimi.deib.privacydataflow.utils.RandomDataSource
import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time

import scala.collection.mutable.Map

/**
  * Created by micheleguerriero on 10/07/17.
  */
object Example1 {

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
      nPastCond
      ) =
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
      } catch {
        case e: Exception =>
          e.printStackTrace()
          sys.exit(1)
      }

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setBufferTimeout(bufferTimeout)
    val executionConfig = env.getConfig

    var inputStream = env.addSource(
      new TransactionSource(
        0,
        nDataSubject,
        minIntervalBetweenTransactions,
        maxIntervalBetweenTransactions,
        isNanoSeconds,
        4,
        6,
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

    val contextStream = env.addSource(new PrivateContextSource(0, minIntervalBetweenContextSwitch, maxIntervalBetweenContextSwitch, nContextSwitch, 0))
      .name("context-source")
      .assignAscendingTimestamps(_.timestamp)
      .setParallelism(1)
      .name("context-timestamp-assigner")

    if (privacyOn) {
      val privateInput: DataSubjectSpecificPrivateStream[FinancialTransaction] = new DataSubjectSpecificPrivateStream[FinancialTransaction](timestampServerIp, timestampServerPort, topologyParallelism)
      privateInput.setStreamToProtect(inputStream)

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

        privateInput.addDataSubjectSpecificStreamPastConditions(randomNumberStream1, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))

        randomNumberStream1
          .writeAsText(pathToResultFolder + "/rnd1.txt")
          .setParallelism(1)
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

        privateInput.addDataSubjectSpecificStreamPastConditions(randomNumberStream2, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))

        randomNumberStream2
          .writeAsText(pathToResultFolder + "/rnd2.txt")
          .setParallelism(1)
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

        privateInput.addDataSubjectSpecificStreamPastConditions(randomNumberStream3, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))

        randomNumberStream3
          .writeAsText(pathToResultFolder + "/rnd3.txt")
          .setParallelism(1)
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

        privateInput.addDataSubjectSpecificStreamPastConditions(randomNumberStream4, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))

        randomNumberStream4
          .writeAsText(pathToResultFolder + "/rnd4.txt")
          .setParallelism(1)
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

        privateInput.addDataSubjectSpecificStreamPastConditions(randomNumberStream5, Map("ds1" -> new PastCondition("data", "Integer", Operator.GRATER, new Integer(90), 30)))

        randomNumberStream5
          .writeAsText(pathToResultFolder + "/rnd5.txt")
          .setParallelism(1)
      }

      privateInput.addContextualPattern("ds1", new PrivacyContext("marketing", "u1", -1), List(new ContextCondition("amount", "Integer", Operator.GRATER, new Integer(5))))
      privateInput.addGeneralizationVector("ds1", new GeneralizationVector(Map("amount" -> 1)))
      privateInput.addGeneralizationFunction("ds1", "amount", 1, _ => new Integer(-1))

      privateInput.finilize(env, contextStream)
        .writeAsText(pathToResultFolder + "/privateInput.txt")
        .setParallelism(1)

    }

    inputStream
      .writeAsText(pathToResultFolder + "/input.txt")
      .setParallelism(1)

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

    //TimestampedFileMerger.merge(new File(pathToResultFolder + "/amount.txt"), new File(pathToResultFolder + "/tCount.txt"), new File(pathToResultFolder + "/merged-tmp.txt"))
    //TimestampedFileMerger.merge(new File(pathToResultFolder + "/context.txt"), new File(pathToResultFolder + "/merged-tmp.txt"), new File(pathToResultFolder + "/merged.log"))

  }
}
