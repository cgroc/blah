package uk.co.grokemon.blah

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import io.circe._
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import fs2.kafka._
import fs2.Stream
import cats.effect.Sync
import scala.util.control.NoStackTrace

object Main extends IOApp {
  
  final case class MyMessage(sup: String)

  implicit val myMessageDecoder: Decoder[MyMessage] = deriveDecoder

  final case class DecodingException(oopsie: String) extends NoStackTrace

  implicit def myMessageDeserialiser[F[_]: Sync]: Deserializer[F, MyMessage] =
    Deserializer.string[F].map {
      decode[MyMessage](_) match {
        case Right(r) => r
        case Left(err) => throw new DecodingException(s"$err")
      }
    }.handleErrorWith {
      case oopsie: DecodingException => Deserializer.fail(oopsie)
    }

  def run(args: List[String]): IO[ExitCode] =
    thing.compile.drain.as(ExitCode.Success)
  
  val consumerSettings: ConsumerSettings[IO, Option[String], Option[String]] = 
    ConsumerSettings[IO, Option[String], Option[String]]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group")

  def thing: Stream[IO, Unit] =
    consumerStream[IO].using(consumerSettings)
      .evalTap(_.subscribeTo("test"))
      .flatMap(_.stream)
      .map { c => //println(c)
        val r = c.record
        println(s"${r.key}, ${r.value}")
      }
}
