package filesharer

package object config {

  case class Configuration(server: ServerConfiguration)

  case class ServerConfiguration(host: String, port: Int)

}
