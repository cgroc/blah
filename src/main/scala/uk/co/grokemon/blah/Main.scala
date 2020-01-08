package uk.co.grokemon.blah

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import fs2.kafka._
import fs2.Stream
import cats.effect.Sync
import scala.util.control.NoStackTrace

import com.ovoenergy.kafka.serialization.core._
import com.ovoenergy.kafka.serialization.avro4s2._

import com.sksamuel.avro4s._

object Main extends IOApp {

  val schemaRegistryEndpoint: String = "http://localhost:8081"

  final case class MyMessage(sup: String)

  def run(args: List[String]): IO[ExitCode] =
    thing.merge(producerStream).compile.drain.as(ExitCode.Success)
  
  implicit val myMessageFromRecord: FromRecord[MyMessage] = FromRecord[MyMessage]

  implicit val valueDeserializer: Deserializer[IO, MyMessage] = 
    Deserializer.delegate(avroBinarySchemaIdDeserializer[MyMessage](schemaRegistryEndpoint, isKey = false, includesFormatByte = true))

  val consumerSettings: ConsumerSettings[IO, Option[String], MyMessage] = 
    ConsumerSettings[IO, Option[String], MyMessage]
      .withAutoOffsetReset(AutoOffsetReset.Latest)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group")

  def thing: Stream[IO, Unit] =
    consumerStream[IO].using(consumerSettings)
      .evalTap(_.subscribeTo("test"))
      .flatMap(_.stream)
      .map { c =>
        val r = c.record
        println(s"Received message! === ${r.value} ===")
      }

  implicit val myMessageToRecord: ToRecord[MyMessage] = ToRecord[MyMessage]

  implicit val valueSerializer: Serializer[IO, MyMessage] = 
    Serializer.delegate(avroBinarySchemaIdSerializer[MyMessage](schemaRegistryEndpoint, isKey = false, includesFormatByte = true))

  val producerSettings: ProducerSettings[IO, Option[String], MyMessage] =
    ProducerSettings[IO, Option[String], MyMessage]
      .withBootstrapServers("localhost:9092")

  def producerStream = Stream.repeatEval {
    IO {
      println("Enter message:\n")
      val message = scala.io.StdIn.readLine()
      ProducerRecords.one(ProducerRecord("test", None, MyMessage(message)))
    }
  }.through(produce(producerSettings))
  
}
