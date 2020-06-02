package filesharer.filename

import cats.effect.IO
import org.specs2.mutable.Specification

class FilenameGeneratorTest extends Specification {

  val filenameGenerator: FilenameGenerator[IO] = FilenameGenerator.impl[IO]

  private val UuidRegex =
    "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}(.+)".r

  "FilenameGenerator" >> {
    "generates a random uuid filename with the original filename extension" >> {
      filenameGenerator.generate("file.png").unsafeRunSync() match {
        case UuidRegex(result) => result.endsWith(".png") must beTrue
      }
    }
  }
}
