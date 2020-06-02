package filesharer

import java.util.concurrent.Executors

import blobstore.Store
import blobstore.gcs.GcsStore
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, ExitCode, Timer}
import com.google.cloud.storage.{Storage, StorageOptions}
import filesharer.filename.FilenameGenerator
import filesharer.upload.UploadFile
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext

object FileSharerServer {

  private val HttpHost = "0.0.0.0"

  def stream[F[_]: ConcurrentEffect](
      implicit T: Timer[F],
      C: ContextShift[F]
  ): Stream[F, ExitCode] = {

    val blocker: Blocker =
      Blocker.liftExecutionContext(
        ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
      )

    val storage: Storage         = StorageOptions.getDefaultInstance.getService
    implicit val store: Store[F] = GcsStore(storage, blocker, List.empty)
    implicit val filenameGenerator: FilenameGenerator[F] =
      FilenameGenerator.impl

    val httpApp =
      FileSharerRoutes.uploadRoutes[F](UploadFile.impl[F]).orNotFound

    val finalHttpApp =
      Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

    BlazeServerBuilder[F]
      .bindHttp(8080, HttpHost)
      .withHttpApp(finalHttpApp)
      .serve
  }

}
