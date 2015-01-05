package shop.infrastructure

import slick.driver.PostgresDriver.simple._
// import org.joda.time.DateTime
// import com.github.tototoshi.slick.PostgresJodaSupport._
import shop.model._
import scala.slick.jdbc.meta.MTable


class ShoppingItemSchema(tag: Tag) extends Table[(Long,String,String,Long)](tag,"shopping_item"){
  def id          = column[Long]("id",O.PrimaryKey,O.AutoInc)
  def name        = column[String]("itemname")
  def description = column[String]("description")
  def listId      = column[Long]("list_id")
  def * = (id, name, description, listId)
}


class ShoppingItemRepository(implicit val registry: ComponentRegistry) extends Repository {

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

   def update(item: ShoppingItem) = {
      database.withSession{ implicit session =>
        item.id.map{ id =>
          val nameQuery = for {
            dbItem <- shoppingItems if dbItem.id === id
          } yield dbItem.name
          nameQuery.update(item.name)
          item
        }
      }
   }

}
