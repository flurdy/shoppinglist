package shop.model

import shop.infrastructure._


case class Shopper(id: Option[Long], username: String){

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
      registry.shopperRepository.save(username).map( newId => this.copy(id=Some(newId) ) )
   }

}

object Shoppers {

   def findShopper(username: String)(implicit registry: ComponentRegistry) = registry.shopperRepository.findShopper(username)

}
