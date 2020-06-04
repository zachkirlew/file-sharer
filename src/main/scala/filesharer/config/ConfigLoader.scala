package filesharer.config

import cats.effect.Sync
import pureconfig._
import pureconfig.generic.auto._

trait ConfigLoader[F[_]] {
  def load(): F[Configuration]
}

object ConfigLoader {

  def impl[F[_]: Sync]: ConfigLoader[F] = new ConfigLoader[F] {
    override def load(): F[Configuration] = {
      Sync[F].delay(ConfigSource.default.loadOrThrow[Configuration])
    }
  }

}
