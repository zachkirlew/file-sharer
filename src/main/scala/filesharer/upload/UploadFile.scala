package filesharer.upload

import cats.Applicative
import cats.implicits._

trait UploadFile[F[_]] {
  def upload(filename: Filename): F[Message]
}

object UploadFile {

  implicit def apply[F[_]](implicit ev: UploadFile[F]): UploadFile[F] = ev

  def impl[F[_]: Applicative]: UploadFile[F] = new UploadFile[F] {
    def upload(n: Filename): F[Message] =
      Message("Uploading " + n.filename).pure[F]
  }

}
