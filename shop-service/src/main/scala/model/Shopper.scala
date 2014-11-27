package shop.model

import shop.infrastructure._


case class Shopper(id: Option[Long], username: String){

   def this(id: Long, username: String) = this(Some(id),username)

   def findLists: Seq[ShoppingList] = for {
         shopperId <- id.toList
         list      <- ShoppingListRepository.findOwnerLists(shopperId)
      } yield list

   def findOtherLists: Seq[ShoppingList] = for {
         shopperId <- id.toList
         list      <- ShoppingListRepository.findParticipantLists(shopperId)
      } yield list

}

object Shoppers {

   def findShopper(username: String) = ShopperRepository.findShopper(username)

}
