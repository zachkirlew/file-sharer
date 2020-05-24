package filesharer.upload

import java.io.File
import java.nio.file.Paths

import blobstore.Store
import blobstore.fs.FileStore
import cats.effect.{Blocker, ContextShift, IO}
import filesharer.FileSharerRoutes
import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.implicits._
import org.http4s.multipart.{Multipart, Part}
import org.specs2.specification.AfterAll

import scala.concurrent.ExecutionContext

class UploadFileSpec extends org.specs2.mutable.Specification with AfterAll {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val blocker: Blocker     = Blocker.liftExecutionContext(ExecutionContext.global)
  implicit val store: Store[IO]     = new FileStore[IO](Paths.get(UploadFileSpec.TempDir), blocker)

  override def afterAll(): Unit = {
    new File(UploadFileSpec.TestFilePath).delete()
    new File(UploadFileSpec.TempDir).delete()
  }

  "UploadFile" >> {
    "uploads file to the store" >> {
      uploadFileResponse.status must beEqualTo(Status.Created)
      new File(UploadFileSpec.TestFilePath).exists must beTrue
    }
  }

  private[this] def uploadFileResponse: Response[IO] = {
    val file = new File(getClass.getResource(s"/${UploadFileSpec.TestFile}").toURI)
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

}

object UploadFileSpec {
  private val TestFile     = "ball.png"
  private val TempDir      = "tmp"
  private val TestFilePath = s"$TempDir/file-sharer-uploads/$TestFile"
}
