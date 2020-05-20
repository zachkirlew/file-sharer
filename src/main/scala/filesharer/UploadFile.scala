package filesharer

import cats.Applicative
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait UploadFile[F[_]] {
  def upload(n: UploadFile.Filename): F[UploadFile.Message]
}

object UploadFile {
  implicit def apply[F[_]](implicit ev: UploadFile[F]): UploadFile[F] = ev

  final case class Filename(filename: String) extends AnyVal

  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  final case class Message(message: String) extends AnyVal
  object Message {
    implicit val messageEncoder: Encoder[Message] = new Encoder[Message] {
      final def apply(a: Message): Json =
        Json.obj(("message", Json.fromString(a.message)))
    }
    implicit def messageEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Message] =
      jsonEncoderOf[F, Message]
  }

  def impl[F[_]: Applicative]: UploadFile[F] = new UploadFile[F] {
    def upload(n: UploadFile.Filename): F[UploadFile.Message] =
      Message("Uploading " + n.filename).pure[F]
  }
}
