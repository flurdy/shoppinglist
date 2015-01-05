package shop.infrastructure

import slick.driver.PostgresDriver.simple._
// import org.joda.time.DateTime
// import com.github.tototoshi.slick.PostgresJodaSupport._
import shop.model._
import scala.slick.jdbc.meta.MTable


class ShoppingListSchema(tag: Tag) extends Table[(Long,String,Long)](tag,"shopping_list"){
  def id      = column[Long]("id",O.PrimaryKey,O.AutoInc)
  def name    = column[String]("listname")
  def ownerId = column[Long]("owner_id")
  def * = (id, name, ownerId)
}

class ListParticipantSchema(tag: Tag) extends Table[(Long,Long,Long)](tag,"list_participant"){
  def id      = column[Long]("id",O.PrimaryKey,O.AutoInc)
  def listId      = column[Long]("list_id")
  def participantId = column[Long]("participant_id")
  def * = (id, listId, participantId)
}


class ShoppingListRepository(implicit val registry: ComponentRegistry) extends Repository with Logging {

   def findOwnerLists(shopperId: Long): Seq[ShoppingList] = {
      database.withSession{ implicit session =>
         shoppingLists.
            filter( _.ownerId === shopperId).
            list.flatMap{
               case (id,_,_) => findList(id)
            }
      }
   }

   def findParticipantLists(shopperId: Long): Seq[ShoppingList] = {
      database.withSession{ implicit session =>
         ( for{
            participant <- listParticipants if participant.participantId === shopperId
            list        <- shoppingLists    if list.id === participant.listId
            shopper     <- shoppers         if shopper.id === list.ownerId
         } yield (list.id, list.name, shopper.id, shopper.username ) ).list.map{
            case (listId, listName, shopperId, username) =>
               new ShoppingList(listId,listName, new Shopper(shopperId,username))
         }
      }
   }

   def addParticipantToList(shopperId: Long, listId: Long): Option[Long] = {
      database.withSession{ implicit session =>
        Some( (listParticipants returning listParticipants.map(_.id)) += (-1,listId,shopperId) )
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
