package shop.model

import shop.infrastructure._

case class ShoppingList(id: Option[Long], name: String, owner: Shopper, items: Seq[ShoppingItem]){

   def this(id: Option[Long], name: String, owner: Shopper) = this(id,name,owner,Seq.empty)

   def this(id: Long, name: String, owner: Shopper) = this(Some(id),name,owner,Seq.empty)

   def save: Option[ShoppingList] = ShoppingListRepository.save(this).map( newId => this.copy(id=Some(newId)) )

   def findItems: Seq[ShoppingItem] = for{
         listId <- id.toList
         item   <- ShoppingItemRepository.findItems(listId)
      } yield item

   def findItem(itemId: Long): Option[ShoppingItem] =
         id.flatMap(ShoppingItemRepository.findItem(_,itemId))

}

object ShoppingLists {

   def findList(listId: Long) = ShoppingListRepository.findList(listId)

}


