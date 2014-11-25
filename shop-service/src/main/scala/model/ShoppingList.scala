package shop.model

import shop.infrastructure._

case class ShoppingList(id: Option[Long], name: String, items: Seq[ShoppingItem]){

   def this(id: Option[Long], name: String) = this(id,name,Seq.empty)

   def save: Option[ShoppingList] = ShoppingListRepository.save(this)

   def findItems: Seq[ShoppingItem] = for{
         listId <- id.toList
         item   <- ShoppingListRepository.findItems(listId)
      } yield item

   def findItem(itemId: Long): Option[ShoppingItem] = id.flatMap(ShoppingItemRepository.findItem(_,itemId))

}

object ShoppingLists {

   def findList = ShoppingListRepository.findList _

}


