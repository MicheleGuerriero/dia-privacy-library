package it.polimi.deib.privacydataflow.core

import java.lang.reflect.Field

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, _}
import org.apache.flink.streaming.api.scala.function.AllWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

/**
  * Created by miik on 12/08/17.
  */
class DataSubjectDerivedPrivateStream[T: TypeInformation, S: TypeInformation](var timestampServerIp: String, var timestampServerPort: Integer, var topologyParallelism: Integer) {

  private var intermediateStream: DataStream[(String, S, scala.collection.mutable.ListBuffer[Boolean])] = _

  private var dataSubjectEvictor: DataSubjectEvictor[S] = _

  private var timeWindow: Time = _

  private var inputStream: DataStream[S] = _

  //private var toProtect: DataStream[T] = _

  private var operator: AllWindowFunction[S, T, TimeWindow] = _

  def setStreamToProtect(inputStream: DataStream[S], operator: AllWindowFunction[S, T, TimeWindow], timeWindow: Time): Unit = {

    this.dataSubjectEvictor = new DataSubjectEvictor[S](timestampServerIp, timestampServerPort)

    this.inputStream = inputStream

    //this.toProtect = toProtect

    this.timeWindow = timeWindow

    this.operator = operator

    implicit val typeInfo = TypeInformation.of(classOf[(String, S, scala.collection.mutable.ListBuffer[Boolean])])

    this.intermediateStream = this.inputStream.map((t: S) => {
      val f: Field = t.getClass.getDeclaredField("dataSubject")
      f.setAccessible(true)

      (f.get(t).asInstanceOf[String] ,t, scala.collection.mutable.ListBuffer[Boolean]())
    }).setParallelism(topologyParallelism)

  }

  def addContextualPattern(dataSubject: String, cp: PrivacyContext, conds: List[ContextCondition]): Unit = {
    this.dataSubjectEvictor.addContextualPattern(dataSubject, cp, conds)
  }

  def addDataSubjectSpecificStreamPastConditions[U](otherDataSubjectSpecificStream: DataStream[U], conds: scala.collection.mutable.Map[String, PastCondition]): Unit = {
    val pastPolicyChecker = new DataSubjectPastConditionChecker[S, U]()

    pastPolicyChecker.setPastConditions(conds)

    implicit val typeInfo = TypeInformation.of(classOf[(String, S, scala.collection.mutable.ListBuffer[Boolean])])
    implicit val typeInfo2 = TypeInformation.of(classOf[(String, U)])

    val intermediateOtherStream = otherDataSubjectSpecificStream.map((t: U) => {
      val f: Field = t.getClass.getDeclaredField("dataSubject")
      f.setAccessible(true)

      (f.get(t).asInstanceOf[String] ,t)
    }).setParallelism(topologyParallelism)


    this.intermediateStream = this.intermediateStream
      //.keyBy(_._1)
      .connect(intermediateOtherStream)//.keyBy(_._1))
      .keyBy(_._1, _._1)
      .flatMap(pastPolicyChecker)
      .setParallelism(topologyParallelism)
  }

  def addGenericStreamPastConditions[U](otherGenericStream: DataStream[U], conds: scala.collection.mutable.Map[String, PastCondition]): Unit = {
    val pastPolicyChecker = new GenericPastConditionChecker[S, U]()

    pastPolicyChecker.setPastConditions(conds)

    implicit val typeInfo = TypeInformation.of(classOf[(String, S, scala.collection.mutable.ListBuffer[Boolean])])

    this.intermediateStream = this.intermediateStream
      .connect(otherGenericStream)
      .flatMap(pastPolicyChecker)
      .setParallelism(1)
  }

  def finilize(env: StreamExecutionEnvironment, contextStream: DataStream[PrivacyContext]): DataStream[T] = {

    this.intermediateStream
      .connect(contextStream.broadcast)
      .flatMap(this.dataSubjectEvictor)
      .setParallelism(1)
      .timeWindowAll(this.timeWindow)
      .apply(this.operator)
      .setParallelism(1)

  }
}