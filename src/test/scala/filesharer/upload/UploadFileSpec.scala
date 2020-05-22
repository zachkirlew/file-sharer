package filesharer.upload

import java.io.{File, FileInputStream}

import cats.effect.{Blocker, ContextShift, IO, Resource}
import filesharer.FileSharerRoutes
import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import fs2._
import org.http4s.multipart.{Multipart, Part}

import scala.concurrent.ExecutionContext

class UploadFileSpec extends org.specs2.mutable.Specification {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val blocker: Blocker     = Blocker.liftExecutionContext(ExecutionContext.global)

  "UploadFile" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return uploading file" >> {
      uriReturnsUploadingFile()
    }
  }

  private[this] def retUploadingFileMessage: Response[IO] = {
    val file = new File(getClass.getResource("/ball.png").toURI)
    val field = Part
      .fileData[IO]("file", file, blocker, `Content-Type`(MediaType.image.png))
    val multipart = Multipart[IO](Vector(field))
    val entity    = EntityEncoder[IO, Multipart[IO]].toEntity(multipart)
    val body      = entity.body
    val postUpload =
      Request[IO](method = Method.POST, uri = uri"/upload", body = body, headers = multipart.headers)
    val uploadFile = UploadFile.impl[IO]
    FileSharerRoutes
      .uploadRoutes(uploadFile)
      .orNotFound(postUpload)
      .unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retUploadingFileMessage.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsUploadingFile(): MatchResult[String] =
    retUploadingFileMessage.as[String].unsafeRunSync() must beEqualTo(
      "{\"message\":\"Uploading ball.png\"}"
    )
}
