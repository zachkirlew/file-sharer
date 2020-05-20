package filesharer

import cats.Applicative
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

package object upload {

  final case class Filename(filename: String) extends AnyVal

  final case class Message(message: String) extends AnyVal

  object Message {
    implicit val messageEncoder: Encoder[Message] = new Encoder[Message] {
      final def apply(a: Message): Json =
        Json.obj(("message", Json.fromString(a.message)))
    }
    implicit def messageEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Message] =
      jsonEncoderOf[F, Message]
  }
}
