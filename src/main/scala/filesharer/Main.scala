package filesharer

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    FileSharerServer.build[IO].as(ExitCode.Success)

}
