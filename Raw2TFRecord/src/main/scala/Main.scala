package com.RawFeature2TFRecord.SingleCategoryFeature

import org.apache.spark.sql.SparkSession

/**
  * Created by yewen1 on 2018/11/7.
  */

case class Config(
  htable_raw_feature: String = "",
  htable_fea_cls: String = "",
  hdfs_tfrecord: String = ""
)

object Main {

  def main(args: Array[String]):Unit={
    val parser = new scopt.OptionParser[Config]("scopt") {
      head("pu_learning", "1.0")

      opt[String]('r', "htable_raw_feature").action( (x, c) => c.copy(htable_raw_feature = x) ).text("htable_raw_feature")
      opt[String]('c', "htable_fea_cls").action((x, c) => c.copy(htable_fea_cls = x)).text("htable_fea_cls")
      opt[String]('t', "hdfs_tfrecord").action((x, c) => c.copy(hdfs_tfrecord = x)).text("hdfs_tfrecord")

      help("help").text("prints this usage text")
    }
    parser.parse(args, Config()) match {
      case Some(config) =>
        val session = SparkSession
          .builder()
          .appName("TFRecord Transformer")
          .config("hive.exec.scratchdir", "./hive_ads_dm")
          .config("spark.sql.warehouse.dir", "./warehouse")
          .enableHiveSupport()
          .getOrCreate()
        val args = Args(session, config)
        RawFeature2TFRecord.transform(session, args)
        session.stop()

      case None =>

    }
  }

}
