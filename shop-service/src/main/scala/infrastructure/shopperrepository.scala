package shop.infrastructure

import slick.driver.PostgresDriver.simple._
// import org.joda.time.DateTime
// import com.github.tototoshi.slick.PostgresJodaSupport._
import shop.model._
import scala.slick.jdbc.meta.MTable


class ShopperSchema(tag: Tag) extends Table[(Long,String)](tag,"shopper"){
  def id       = column[Long]("id",O.PrimaryKey)
  def username = column[String]("username")
  def * = (id, username) // <> (Shopper.tupled,Shopper.unapply)
}

class IdentitySchema(tag: Tag) extends Table[(Long,String,Long)](tag,"shopper_identity"){
  def id        = column[Long]("id",O.PrimaryKey)
  def password  = column[String]("[password]")
  def shopperId = column[Long]("shopper_id")
  def * = (id, password, shopperId)
}

class ShoppingListSchema(tag: Tag) extends Table[(Long,String,Long)](tag,"shopping_list"){
  def id      = column[Long]("id",O.PrimaryKey)
  def name    = column[String]("listname")
  def ownerId = column[Long]("owner_id")
  def * = (id, name, ownerId)
}

class ShoppingItemSchema(tag: Tag) extends Table[(Long,String,String,Long)](tag,"shopping_item"){
  def id          = column[Long]("id",O.PrimaryKey)
  def name        = column[String]("itemname")
  def description = column[String]("description")
  def listId      = column[Long]("list_id")
  def * = (id, name, description, listId)
}


object RepositoryInitialiser extends Repository {

   database.withSession{ implicit session =>
      if (MTable.getTables("shopper").list.isEmpty) {
         createTables
      }
   }

   def createTables {
      database.withSession{ implicit session =>
         (shoppers.ddl ++ identities.ddl ++ shoppingLists.ddl ++ shoppingItems.ddl).create
      }
   }

}


trait Repository {

   val datasourceConfig: DatasourceConfig = Environment.datasourceConfig

   lazy val database = Database.forDataSource(datasourceConfig.datasource)

   val shoppers      = TableQuery[ShopperSchema]
   val identities    = TableQuery[IdentitySchema]
   val shoppingLists = TableQuery[ShoppingListSchema]
   val shoppingItems = TableQuery[ShoppingItemSchema]

}


object IdentityRepository extends Repository {

   def save(shopperId: Long, password: String)(implicit session: Session): Long = {
      (identities returning identities.map(_.id)) += (-1,password,shopperId)
   }

   def findEncryptedPassword(username: String): Option[String] = {
      for{
         shopper   <- ShopperRepository.findShopper(username)
         shopperId <- shopper.id
         password  <- findEncryptedPassword(shopperId)
      } yield password
   }

   private def findEncryptedPassword(shopperId: Long): Option[String] = {
      database.withSession{ implicit session =>
         identities.
            filter( _.shopperId === shopperId ).
            map(_.password).
            firstOption
      }
   }

}


object ShopperRepository extends Repository {

   def save(username: String, password: String): Option[Long] = {
      database.withSession{ implicit session =>
         findShopper(username) match {
            case None => {
               val shopperId = (shoppers returning shoppers.map(_.id)) += (-1,username)
               IdentityRepository.save(shopperId,password)
               Some(shopperId)
            }
            case _ => None
         }
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

   def findShopper(shopperId: Long): Option[Shopper] = {
      database.withSession{ implicit session =>
         shoppers.filter(_.id === shopperId).
            firstOption.map{
               case (id,username) => new Shopper(id,username)
            }
      }
   }

}

object ShoppingListRepository extends Repository {

   def findOwnerLists(shopperId: Long): Seq[ShoppingList] = {
      database.withSession{ implicit session =>
         shoppingLists.
            filter( _.ownerId === shopperId).
            list.flatMap{
               case (id,_,_) => findList(id)
            }
      }
   }

   def findList(listId: Long): Option[ShoppingList] = {
      database.withSession{ implicit session =>
         ( for{
            list <- shoppingLists
            if( list.id === listId)
            shopper <- shoppers
            if( shopper.id === list.ownerId)
         } yield (list.id, list.name, shopper.id, shopper.username )).
            firstOption.map{
               case (listId, listName, shopperId, username) =>
                  new ShoppingList(listId,listName, new Shopper(shopperId,username))
            }
      }
   }

   def save(list: ShoppingList): Option[Long] = {
      database.withSession{ implicit session =>
         list.owner.id.map { shopperId =>
            (shoppingLists returning shoppingLists.map(_.id)) += (-1,list.name,shopperId)
         }
      }
   }


   def findItemList(itemId: Long): Option[ShoppingList] = {
      database.withSession{ implicit session =>
         ( for {
            item <- shoppingItems if item.id === itemId
            list <- shoppingLists if list.id === item.listId
            shopper <- shoppers   if( shopper.id === list.ownerId)
         } yield (list.id,list.name, shopper.id, shopper.username) ).firstOption.map{
            case (listId,listName,shopperId,username) => new ShoppingList(listId,listName, new Shopper(shopperId,username))
         }
      }
   }
}

object ShoppingItemRepository extends Repository {

   def findItems(listId: Long): Seq[ShoppingItem] = {
      database.withSession{ implicit session =>
         ( for {
            item <- shoppingItems if item.listId === listId
         } yield (item.id,item.name) ).list.map{
            case (itemId,itemName) => new ShoppingItem(itemId,itemName)
         }
      }
   }

   def findItem(listId: Long, itemId: Long): Option[ShoppingItem] = {
      database.withSession{ implicit session =>
         ( for {
            item <- shoppingItems
               if item.listId === listId
               if item.id     === itemId
         } yield (item.id,item.name) ).firstOption.map{
            case (itemId,itemName) => new ShoppingItem(itemId,itemName)
         }
      }
   }

   def save(list: ShoppingList, item: ShoppingItem): Option[Long] = {
      database.withSession{ implicit session =>
         list.id.map { listId =>
            (shoppingItems returning shoppingItems.map(_.id)) += (-1,item.name,item.description.getOrElse(""),listId)
         }
      }
   }

}
