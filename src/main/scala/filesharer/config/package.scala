package filesharer

package object config {

  case class Configuration(server: ServerConfiguration,
                           database: DatabaseConfiguration)

  case class ServerConfiguration(host: String, port: Int)

  case class DatabaseConfiguration(driver: String,
                                   url: String,
                                   user: String,
                                   password: String,
                                   threadPoolSize: Int)
}
