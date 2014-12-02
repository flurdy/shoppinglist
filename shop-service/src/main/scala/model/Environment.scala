package shop.model

import com.github.t3hnar.bcrypt._
import spray.util.LoggingContext
// import akka.event.Logging
import shop.infrastructure._
import com.jolbox.bonecp.BoneCPDataSource
import javax.sql.DataSource
import com.typesafe.config.{Config,ConfigFactory}
import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
// import ch.qos.logback.classic.LoggerContext


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

  private val config = ConfigFactory.load()

  lazy val datasourceConfig = findDatasource

  private def findDatasource: DatasourceConfig = {
    val driver = config.getString(s"datasource.driver")
    val url = config.getString(s"datasource.url")
    val username = config.getString(s"datasource.username")
    val password = config.getString(s"datasource.password")
    DatasourceConfig(driver,url,username,password)
  }

}

trait Logging {
   def logger = LoggerFactory.getLogger(this.getClass)
}


trait ComponentRegistry {

   // implicit val registry = this

   val datasourceConfig: DatasourceConfig

   val shopperRepository: ShopperRepository

   val identityRepository: IdentityRepository

   val shoppingListRepository: ShoppingListRepository

   val shoppingItemRepository: ShoppingItemRepository

}

class RuntimeComponentRegistry extends ComponentRegistry {

   val datasourceConfig = Environment.datasourceConfig

   val shopperRepository = new ShopperRepository()(this)

   val identityRepository = new IdentityRepository()(this)

   val shoppingListRepository = new ShoppingListRepository()(this)

   val shoppingItemRepository = new ShoppingItemRepository()(this)

}
