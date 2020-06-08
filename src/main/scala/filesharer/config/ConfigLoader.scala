package filesharer.config

import cats.effect.{Blocker, ContextShift, Resource, Sync}
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

trait ConfigLoader[F[_]] {
  def load(): Resource[F, Configuration]
}

object ConfigLoader {

  private final val ConfigFile = "application.conf"

  def impl[F[_]: Sync](implicit cs: ContextShift[F]): ConfigLoader[F] =
    new ConfigLoader[F] {
      override def load(): Resource[F, Configuration] = Blocker[F].flatMap {
        blocker =>
          Resource.liftF(
            ConfigSource
              .fromConfig(ConfigFactory.load(ConfigFile))
              .loadF[F, Configuration](blocker)
          )
      }
    }
}
