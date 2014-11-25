package shop.model

import shop.infrastructure._


case class Shopper(id: Option[Long], username: String){

   def findLists: Seq[ShoppingList] = for {
         shopperId <- id.toList
         list      <- ShoppingListRepository.findOwnerLists(shopperId)
      } yield list

   // TODO
   def findOtherLists: Seq[ShoppingList] = Seq.empty
   // id.map(ShopperRepository.findParticipantLists(_))

}

object Shoppers {
   
   def findShopper = ShopperRepository.findShopper _

}
