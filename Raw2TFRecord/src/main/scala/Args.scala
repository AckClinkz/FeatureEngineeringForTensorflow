package com.sina.yewen1.RawFeature2TFRecord.SingleCategoryFeature

import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.broadcast.Broadcast

/**
  * Created by yewen1 on 2018/11/7.
  */
class Args(@transient session: SparkSession, @transient config: Config) extends Serializable{
  val htable_raw_feature: String = config.htable_raw_feature
  val hdfs_tfrecord: String = config.hdfs_tfrecord
  val bc_fea_cls: Broadcast[Set[String]] = this.init_fea_cls(config.htable_fea_cls)

  def init_fea_cls(htable_name: String): Broadcast[Set[String]] = {
    val df_fea_cls: DataFrame = session.sqlContext.sql(String.format("SELECT * FROM %s", htable_name))
    val fea_cls: Set[String] = df_fea_cls.collect().map{
      case Row(cls: String) => cls
    }.toSet
    session.sparkContext.broadcast(fea_cls)
  }

}

object Args{

  def apply(session: SparkSession, config: Config) =
    new Args(session, config)
}