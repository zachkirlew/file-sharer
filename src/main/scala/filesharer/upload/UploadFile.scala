package filesharer.upload

import blobstore.{Path, Store}
import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import filesharer.filename.FilenameGenerator
import org.http4s.multipart.Part

trait UploadFile[F[_]] {
  def upload(part: Part[F]): F[Unit]
}

object UploadFile {

  private val StorePath = "file-sharer-uploads/"

  def impl[F[_]: Sync](implicit store: Store[F], generator: FilenameGenerator[F]): UploadFile[F] =
    new UploadFile[F] {

      def upload(part: Part[F]): F[Unit] = {
        generator
          .generate(part.filename.getOrElse(""))
          .map(StorePath.concat)
          .flatMap(
            path => part.body.through(store.put(Path(path))).compile.drain
          )
      }
    }
}
