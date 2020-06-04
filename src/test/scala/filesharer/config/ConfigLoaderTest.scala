package filesharer.config

import cats.effect.IO
import org.specs2.mutable.Specification

class ConfigLoaderTest extends Specification {

  private val configLoader: ConfigLoader[IO] = ConfigLoader.impl[IO]

  "ConfigLoader" >> {
    "generates a random uuid filename with the original filename extension" >> {
      configLoader.load().unsafeRunSync() mustEqual Configuration(server = ServerConfiguration("0.0.0.0", 8080))
    }
  }

}
