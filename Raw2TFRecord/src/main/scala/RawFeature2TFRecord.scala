package com.RawFeature2TFRecord.SingleCategoryFeature

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types._

/**
  * Created by yewen1 on 2018/11/7.
  */
object RawFeature2TFRecord {

  val func_split = udf((raw_feature: String) => raw_feature.split(",").toSeq)

  def init_schema(bc_fea_cls: Broadcast[Set[String]]): (StructType, Seq[String]) = {
    val schema_label = List(StructField("label", StringType))
    val schema_feature = bc_fea_cls.value.map(x => StructField(x, ArrayType(StringType, true))).toList
    val schema = StructType(schema_label ++ schema_feature)
    return (schema, schema.map(_.name))
  }

  def extract_feature(session: SparkSession, df_raw_feature: DataFrame, fields: Seq[String]) :DataFrame={
    fields.filter(_ != "label").foldLeft(df_raw_feature)((df_raw_feature, field_fea) =>{
      // Consider field name as feature's prefix
      // TODO: dynamically configure prefix
      val bc_prefix: Broadcast[String] = session.sparkContext.broadcast(field_fea)
      val func = udf((raw_feature: Seq[String]) => raw_feature.filter(_.contains(bc_prefix.value)))
      df_raw_feature.withColumn(field_fea, func(col("feature")))
    })
  }

  def transform(session: SparkSession, args: Args): Unit={
    // Initialize schema
    val (schema, fields) = this.init_schema(args.bc_fea_cls)
    // Load raw feature
    val df_raw_feature = session.sqlContext.sql("SELECT * FROM %s".format(args.htable_raw_feature)).withColumn(
      "feature", func_split(col("feature"))
    )
    // Extract different class feature from raw feature
    val df_diff_fields_feature = extract_feature(session, df_raw_feature, fields)
    // tfrecord
    val df_tfrecord = session.createDataFrame(
      df_diff_fields_feature.select(fields.head, fields.tail: _*).rdd,
      schema
    )
    df_tfrecord.write.format("tfrecords").option("recordType", "Example").save(args.hdfs_tfrecord)

  }

}
