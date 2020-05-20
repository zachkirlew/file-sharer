package filesharer

import cats.effect.Sync
import cats.implicits._
import filesharer.upload.{Filename, UploadFile}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object FileSharerRoutes {

  def uploadRoutes[F[_]: Sync](U: UploadFile[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "upload" / filename =>
        for {
          message <- U.upload(Filename(filename))
          resp    <- Ok(message)
        } yield resp
    }
  }

}
