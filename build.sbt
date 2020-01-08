val CirceVersion = "0.11.1"
val LogbackVersion = "1.2.3"
val Fs2Kafka = "0.20.2"
val ScalaTest = "3.1.0"
val KafkaSerializationV = "0.5.21"

resolvers in ThisBuild ++= Seq(
  "Ovo" at "https://dl.bintray.com/ovotech/maven/",
  "Confluent" at "http://packages.confluent.io/maven/"
)

lazy val root = (project in file("."))
  .settings(
    organization := "uk.co.grokemon",
    name := "blah",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "com.ovoenergy"   %% "fs2-kafka"                    % Fs2Kafka,
      "io.circe"        %% "circe-generic"                % CirceVersion,
      "io.circe"        %% "circe-parser"                 % CirceVersion,
      "org.scalatest"   %% "scalatest"                    % ScalaTest % "test",
      "ch.qos.logback"  %  "logback-classic"              % LogbackVersion,
      "com.ovoenergy"   %% "kafka-serialization-core"     % KafkaSerializationV,
      "com.ovoenergy"   %% "kafka-serialization-avro4s2"  % KafkaSerializationV
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)
