package filesharer.config

import cats.effect.{Blocker, ContextShift, IO}
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

class ConfigLoaderTest extends Specification {

  private implicit val cs: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)
  private implicit val blocker: Blocker =
    Blocker.liftExecutionContext(ExecutionContext.global)

  private val configLoader: ConfigLoader[IO] = ConfigLoader.impl[IO]

  "ConfigLoader" >> {
    "generates a random uuid filename with the original filename extension" >> {
      configLoader.load().use(IO.pure).unsafeRunSync() mustEqual Configuration(
        server = ServerConfiguration("0.0.0.0", 8080),
        database = DatabaseConfiguration(
          "org.h2.Driver",
          "jdbc:h2:mem:todo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
          "sa",
          "",
          32
        )
      )
    }
  }

}
