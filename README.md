# FeatureEngineeringForTensorflow

It is different from research that there is volumes of data in production environment. Compared to single machine, `Spark` can work fine in this situation. At the same time, `Tensorflow` is an efficient tool to explore big data capability. This project is an example how to preprocess data using Spark and transform to `TFRecord`.

## Building

```
sbt assembly
```

## Running

```shell
spark-submit --class com.RawFeature2TFRecord.SingleCategoryFeature.Main \
 --master yarn-cluster \
 --num-executors 10 \
 --driver-memory 10g \
 --executor-memory 10g \
 --executor-cores 1 \
 --conf spark.kryoserializer.buffer.max=512m \
 --conf spark.yarn.maxAppAttempts=1 \
 --files $YOUR_SPARK_DIR/conf/hive-site.xml \
 ./Raw2TFRecord/target/scala-2.11/Raw2TFRecord-assembly-0.1-SNAPSHOT.jar \
 --htable_raw_feature $YOUR_HIVE_TABLE_FEATURE \
 --htable_fea_cls $YOUR_HIVE_TABLE_FEATURE_CLSS \
 --hdfs_tfrecord $YOUR_HDFS_TFRECORD
```

## TABLE FORMAT

`$YOUR_HIVE_TABLE_FEATURE` is consist of label and features, where different features is separated by commas. `$YOU_HIVE_TABLE_FEATURE_CLSS` save all of feature classes, such as age, gender, and so on. To make it easier understanding, example as follow:

- $YOUR_HIVE_TABLE_FEATURE

```
0  IW_gametest,LIFE_170010103,LOGIN_210002,AGE_29,GEND_2,PROV_311,CITY_31111,PLAT_iphone
1  AGE_29,GEND_2,PROV_311,CITY_31111,PLAT_iphone
```

where every feature is made up of feature class and value.

- $YOU_HIVE_TABLE_FEATURE_CLSS

```
PLAT
PROV
AGE
CITY
IW
LIFE
LOGIN
```
