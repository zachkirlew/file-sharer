package filesharer.filename

import java.util.UUID

import cats.effect.Sync
import cats.syntax.functor._

trait FilenameGenerator[F[_]] {
  def generate(originalFilename: String): F[String]
}

object FilenameGenerator {
  private val ExtensionRegex = """.*(\.\w+)""".r

  def impl[F[_]: Sync]: FilenameGenerator[F] = new FilenameGenerator[F] {
    val uuid: F[String] = Sync[F].delay { UUID.randomUUID().toString }

    override def generate(originalFilename: String): F[String] = {
      uuid.map(id => s"$id${extension(originalFilename)}")
    }

    private def extension(filename: String) = filename match {
      case ExtensionRegex(ext) => ext
      case _                   => ""
    }
  }
}
