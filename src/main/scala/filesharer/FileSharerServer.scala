package filesharer

import java.util.concurrent.Executors

import blobstore.Store
import blobstore.gcs.GcsStore
import cats.effect.{Async, Blocker, ConcurrentEffect, ContextShift, ExitCode, Resource, Timer}
import com.google.cloud.storage.{Storage, StorageOptions}
import doobie.h2.H2Transactor
import filesharer.config.{ConfigLoader, Configuration}
import filesharer.db.Database
import filesharer.filename.FilenameGenerator
import filesharer.upload.UploadFile
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext

object FileSharerServer {

  def build[F[_]: ConcurrentEffect](implicit T: Timer[F],
                                    C: ContextShift[F]): F[ExitCode] = {

    val blocker: Blocker =
      Blocker.liftExecutionContext(
        ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
      )

    val storage: Storage = StorageOptions.getDefaultInstance.getService
    implicit val store: Store[F] = GcsStore(storage, blocker, List.empty)
    implicit val filenameGenerator: FilenameGenerator[F] =
      FilenameGenerator.impl

    val httpApp =
      FileSharerRoutes.uploadRoutes[F](UploadFile.impl[F]).orNotFound

    val finalHttpApp =
      Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

    resources.use {
      case (config, transactor) =>
        BlazeServerBuilder[F]
          .bindHttp(config.server.port, config.server.host)
          .withHttpApp(finalHttpApp)
          .serve
          .compile
          .lastOrError
    }
  }

  private def resources[F[_]: Async](
    implicit contextShift: ContextShift[F]
  ): Resource[F, (Configuration, H2Transactor[F])] = {
    for {
      config <- ConfigLoader.impl[F].load()
      transactor <- Database.transactor(config.database)
    } yield (config, transactor)
  }

}
