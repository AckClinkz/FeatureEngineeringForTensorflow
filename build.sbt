val sparkVersion = "2.2.1"
val protobufVersion = "3.5.1"

lazy val commonDependencies = Seq(
  // spark package
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-hive" % sparkVersion % "provided",
  // parse args package
  "com.github.scopt" %% "scopt" % "3.7.0",
  // tensorflow package
  "com.google.protobuf" % "protobuf-java" % protobufVersion,
  "org.tensorflow" %% "spark-tensorflow-connector" % "1.12.0"
)

lazy val TensorflowOnSpark = project.in(file(".")).aggregate(
  Raw2TFRecord
)

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "algo",
  scalaVersion := "2.11.8",
  test in assembly := {}
)

lazy val Raw2TFRecord = (project in file("Raw2TFRecord"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies,
    assemblyMergeStrategy in assembly := {
      case PathList("org", "tensorflow", xs @ _*)         => MergeStrategy.first
      case PathList("org", "apache", xs @ _*) => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    assemblyShadeRules in assembly := Seq(
      ShadeRule.rename("com.google.protobuf.*" -> "shadedproto.@1").inAll
    )
  )




    