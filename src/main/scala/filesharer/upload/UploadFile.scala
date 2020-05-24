package filesharer.upload

import blobstore.{Path, Store}
import cats.Applicative
import cats.effect.Sync
import org.http4s.multipart.Part

trait UploadFile[F[_]] {
  def upload(part: Part[F]): F[Unit]
}

object UploadFile {

  implicit def apply[F[_]](implicit ev: UploadFile[F]): UploadFile[F] = ev

  def impl[F[_]: Applicative: Sync](implicit store: Store[F]): UploadFile[F] = new UploadFile[F] {
    def upload(part: Part[F]): F[Unit] = {
      val filename = part.filename.getOrElse("file")
      part.body.through(store.put(Path(filename))).compile.drain
    }
  }

}
