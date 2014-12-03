package shop.model

import shop.infrastructure._


case class Shopper(id: Option[Long], username: String) extends Logging {

   def this(id: Long, username: String) = this(Some(id),username)

   def this(username: String) = this(None,username)

   def findLists(implicit registry: ComponentRegistry): Seq[ShoppingList] = for {
         shopperId <- id.toList
         list      <- registry.shoppingListRepository.findOwnerLists(shopperId)
      } yield list

   def findOtherLists(implicit registry: ComponentRegistry): Seq[ShoppingList] = for {
         shopperId <- id.toList
         list      <- registry.shoppingListRepository.findParticipantLists(shopperId)
      } yield list

   def save(implicit registry: ComponentRegistry): Option[Shopper] = {
      logger.info(s"Saving new shopper: $username")
      registry.shopperRepository.findShopper(username) match {
         case None => {
            registry.shopperRepository.save(username).map( newId => this.copy(id=Some(newId) ) )
         }
         case _ => {
            logger.info(s"Username already exists: $username")
            None
         }
      }
   }

   def createInitialList(implicit registry: ComponentRegistry): Option[ShoppingList] = {
      id.flatMap{ shopperId => new ShoppingList(s"${username }'s list",this).save }
   }

}

object Shoppers {

   def findShopper(username: String)(implicit registry: ComponentRegistry) = registry.shopperRepository.findShopper(username)

}
