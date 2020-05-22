package filesharer

import cats.effect.Sync
import cats.implicits._
import filesharer.upload.{Filename, UploadFile}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.multipart.Multipart

object FileSharerRoutes {

  def uploadRoutes[F[_]: Sync](U: UploadFile[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / "upload" =>
        req.decode[Multipart[F]] { m =>
          {
            m.parts.find(_.name.contains("file")) match {
              case None => BadRequest(s"Not file")
              case Some(part) =>
                for {
                  message <- U.upload(Filename(part.filename.getOrElse("unknown file")))
                  resp    <- Ok(message)
                } yield resp
            }
          }
        }
    }
  }
}
