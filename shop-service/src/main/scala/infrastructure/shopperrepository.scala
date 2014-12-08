package shop.infrastructure

import slick.driver.PostgresDriver.simple._
// import org.joda.time.DateTime
// import com.github.tototoshi.slick.PostgresJodaSupport._
import shop.model._
// import akka.event.Logging
import scala.slick.jdbc.meta.MTable


class ShopperSchema(tag: Tag) extends Table[(Long,String)](tag,"shopper"){
  def id       = column[Long]("id",O.PrimaryKey,O.AutoInc)
  def username = column[String]("username")
  def uniqueUsername = index("IDX_SHOPPER_USERNAME", username, unique = true)
  def * = (id, username) // <> (Shopper.tupled,Shopper.unapply)
}

class IdentitySchema(tag: Tag) extends Table[(Long,String,Long)](tag,"shopper_identity"){
  def id        = column[Long]("id",O.PrimaryKey,O.AutoInc)
  def password  = column[String]("password")
  def shopperId = column[Long]("shopper_id")
  // def shopper = foreignKey("IDENTITY_SHOPPER_FK", shopperId, TableQuery[ShopperSchema])(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def * = (id, password, shopperId)
}


class RepositoryInitialiser(implicit val registry: ComponentRegistry) extends Repository {

   def initialiseDatabase = {
      database.withSession{ implicit session =>
         List(shoppers,identities,shoppingLists,shoppingItems,listParticipants).map( createTableIfNotExists(_))
      }
   }

   private def isTableCreated(table: TableQuery[_ <: Table[_]])(implicit session: Session): Boolean = {
      MTable.getTables(table.baseTableRow.tableName).list.isEmpty
   }

   private def createTableIfNotExists(table: TableQuery[_ <: Table[_]])(implicit session: Session){
      if(isTableCreated(table)) table.ddl.create
   }

   def createTables {
      database.withSession{ implicit session =>
         (shoppers.ddl ++ identities.ddl ++ shoppingLists.ddl ++ shoppingItems.ddl ++ listParticipants.ddl).create
      }
   }

   def cleanDatabase = {
      database.withSession{ implicit session =>
         shoppingItems.delete
         listParticipants.delete
         shoppingLists.delete
         identities.delete
         shoppers.delete
      }
   }

}


trait Repository {

   val registry: ComponentRegistry

   lazy val database = Database.forDataSource(registry.datasourceConfig.datasource)

   val shoppers      = TableQuery[ShopperSchema]
   val identities    = TableQuery[IdentitySchema]
   val shoppingLists = TableQuery[ShoppingListSchema]
   val shoppingItems = TableQuery[ShoppingItemSchema]
   val listParticipants = TableQuery[ListParticipantSchema]

}


class IdentityRepository(implicit val registry: ComponentRegistry) extends Repository with Logging {

   val shopperRepository = registry.shopperRepository

   def save(shopperId: Long, password: String): Option[Long] = {
      database.withSession{ implicit session =>
         Some( (identities returning identities.map(_.id) += (-1,password,shopperId)) )
      }
   }

   def findEncryptedPassword(shopperId: Long): Option[String] = {
      database.withSession{ implicit session =>
            logger.info(s"find pw shopper id $shopperId")
         identities.
            filter( _.shopperId === shopperId ).
            map(_.password).
            firstOption
      }
   }

}


class ShopperRepository(implicit val registry: ComponentRegistry) extends Repository with Logging {

   val identityRepository = registry.identityRepository


   def save(username: String): Option[Long] = {
      database.withSession{ implicit session =>
         Some( (shoppers returning shoppers.map(_.id) += (-1,username) ) )
      }
   }

   def findShopper(username: String): Option[Shopper] = {
      database.withSession{ implicit session =>
         shoppers.filter(_.username === username).
            firstOption.map{
               case (id,username) => Shopper(Some(id),username)
            }
      }
   }

   def findShopperById(shopperId: Long): Option[Shopper] = {
      database.withSession{ implicit session =>
        logger.debug(s"Looking for $shopperId")
         shoppers.filter(_.id === shopperId).
            firstOption.map{
               case (id,username) => new Shopper(id,username)
            }
      }
   }

}
