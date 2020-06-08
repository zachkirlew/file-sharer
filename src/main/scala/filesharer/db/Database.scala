package filesharer.db

import cats.effect.{Async, Blocker, ContextShift, Resource}
import doobie.h2.H2Transactor
import doobie.util.ExecutionContexts
import filesharer.config.DatabaseConfiguration

object Database {

  def transactor[F[_]: Async](
    config: DatabaseConfiguration
  )(implicit contextShift: ContextShift[F]): Resource[F, H2Transactor[F]] =
    for {
      execContext <- ExecutionContexts.fixedThreadPool[F](32)
      blocker <- Blocker[F]
      transactor <- H2Transactor.newH2Transactor[F](
        config.driver,
        config.user,
        config.password,
        execContext,
        blocker
      )
    } yield transactor

}
