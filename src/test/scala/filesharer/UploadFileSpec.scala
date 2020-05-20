package filesharer

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

class UploadFileSpec extends org.specs2.mutable.Specification {

  "UploadFile" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return uploading file" >> {
      uriReturnsUploadingFile()
    }
  }

  private[this] val retHelloWorld: Response[IO] = {
    val getHW      = Request[IO](Method.GET, uri"/upload/file")
    val uploadFile = UploadFile.impl[IO]
    FileSharerRoutes
      .uploadRoutes(uploadFile)
      .orNotFound(getHW)
      .unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retHelloWorld.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsUploadingFile(): MatchResult[String] =
    retHelloWorld.as[String].unsafeRunSync() must beEqualTo(
      "{\"message\":\"Uploading file\"}"
    )
}
