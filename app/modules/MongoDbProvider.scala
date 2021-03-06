package modules

import com.google.inject.{Inject, Provider}
import com.typesafe.config.Config
import reactivemongo.api.{DB, MongoConnection}

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class MongoDbProvider @Inject()(config: Config) extends Provider[DB]{
  val database: String = config.getString("mongodb.database")
  val servers: Seq[String] = config.getStringList("mongodb.servers").asScala
  val driver = new reactivemongo.api.MongoDriver
  val connection: MongoConnection = driver.connection(servers)

  override def get(): DB = Await.result(connection.database(database), 10 seconds)
}
