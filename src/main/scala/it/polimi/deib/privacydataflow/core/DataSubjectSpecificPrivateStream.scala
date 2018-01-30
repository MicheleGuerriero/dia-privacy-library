package it.polimi.deib.privacydataflow.core

import java.lang.reflect.Field

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, _}

/**
  * Created by miik on 12/08/17.
  */
class DataSubjectSpecificPrivateStream[T](var timestampServerIp: String, var timestampServerPort: Integer, var topologyParallelism: Integer) {

  private var intermediateStream: DataStream[(String, T, scala.collection.mutable.ListBuffer[Boolean])] = _

  private var viewBuilder: ViewBuilder[T] = new ViewBuilder[T](timestampServerIp, timestampServerPort)

  def setStreamToProtect(toProtect: DataStream[T]): Unit = {

    implicit val typeInfo = TypeInformation.of(classOf[(String, T, scala.collection.mutable.ListBuffer[Boolean])])

    this.intermediateStream = toProtect.map((t: T) => {
      val f: Field = t.getClass.getDeclaredField("dataSubject")
      f.setAccessible(true)

      (f.get(t).asInstanceOf[String] ,t, scala.collection.mutable.ListBuffer[Boolean]())
    }).setParallelism(topologyParallelism)

  }

  def addGeneralizationFunction(dataSubject: String, attribute: String, level: Integer, f: Object => Object): Unit = {
    this.viewBuilder.addGeneralizationFunction(dataSubject, attribute, level, f)
  }

  def addGeneralizationVector(dataSubject: String, gv: GeneralizationVector): Unit = {
    this.viewBuilder.addGeneralizationVector(dataSubject, gv)
  }

  def addContextualPattern(dataSubject: String, cp: PrivacyContext, conds: List[ContextCondition]): Unit = {
    this.viewBuilder.addContextualPattern(dataSubject, cp, conds)
  }

  def addDataSubjectSpecificStreamPastConditions[S](otherDataSubjectSpecificStream: DataStream[S], conds: scala.collection.mutable.Map[String, PastCondition]): Unit = {
    val pastPolicyChecker = new DataSubjectPastConditionChecker[T, S]()

    pastPolicyChecker.setPastConditions(conds)

    implicit val typeInfo = TypeInformation.of(classOf[(String, T, scala.collection.mutable.ListBuffer[Boolean])])
    implicit val typeInfo2 = TypeInformation.of(classOf[(String, S)])

    val intermediateOtherStream = otherDataSubjectSpecificStream.map((t: S) => {
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

  def addGenericStreamPastConditions[S](otherGenericStream: DataStream[S], conds: scala.collection.mutable.Map[String, PastCondition]): Unit = {
    val pastPolicyChecker = new GenericPastConditionChecker[T, S]()

    pastPolicyChecker.setPastConditions(conds)

    implicit val typeInfo = TypeInformation.of(classOf[(String, T, scala.collection.mutable.ListBuffer[Boolean])])

    this.intermediateStream = this.intermediateStream
      .connect(otherGenericStream)
      .flatMap(pastPolicyChecker)
      .setParallelism(1)
  }

  def finilize(env: StreamExecutionEnvironment, contextStream: DataStream[PrivacyContext]): DataStream[(String, T, scala.collection.mutable.ListBuffer[Boolean])] = {

    implicit val typeInfo = TypeInformation.of(classOf[(String, T, scala.collection.mutable.ListBuffer[Boolean])])

    this.intermediateStream
      .connect(contextStream.broadcast)
      //.keyBy(_._1, _.serviceUser)
      .flatMap(this.viewBuilder)
      .setParallelism(1)

  }

}
