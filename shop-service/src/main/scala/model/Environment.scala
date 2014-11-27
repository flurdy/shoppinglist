package shop.model

import com.github.t3hnar.bcrypt._
import spray.util.LoggingContext
import akka.event.Logging
import shop.infrastructure._
import com.jolbox.bonecp.BoneCPDataSource
import javax.sql.DataSource
import com.typesafe.config.{Config,ConfigFactory}

// TODO change to actors

case class RegistrationDetails(username: String, password: String){

   def register: Option[Shopper] = {
      Shoppers.findShopper(username) match {
         case None => {
            val encryptedPassword = password.bcrypt
            ShopperRepository.save(username,encryptedPassword).map { id =>
              new Shopper(id,username)
            }
         }
         case _ => None
      }
   }

}

case class LoginDetails(username: String, password: String){
   def authenticate: Option[Shopper] = {
      for{
         shopper  <- Shoppers.findShopper(username)
         password <- IdentityRepository.findEncryptedPassword(username)
         // if password.isBcrypted(password)
      } yield shopper
   }

}

case class DatasourceConfig(
             driver: String, url: String, username: String, password: String)  {

  def datasource: DataSource = {
    val datasource = new BoneCPDataSource
    datasource.setJdbcUrl(url)
    datasource.setUsername(username)
    datasource.setPassword(password)
    datasource.setDriverClass( classOf[org.postgresql.Driver].getName )
    datasource
  }

}

object Environment {

  val config = ConfigFactory.load()

  lazy val datasourceConfig = findDatasource

  private def findDatasource: DatasourceConfig = {
    val driver = config.getString(s"datasource.driver")
    val url = config.getString(s"datasource.url")
    val username = config.getString(s"datasource.username")
    val password = config.getString(s"datasource.password")
    DatasourceConfig(driver,url,username,password)
  }

}
