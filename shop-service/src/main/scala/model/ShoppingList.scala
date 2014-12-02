package shop.model

import shop.infrastructure._

case class ShoppingList(id: Option[Long], name: String, owner: Shopper, items: Seq[ShoppingItem]){

   def this(id: Option[Long], name: String, owner: Shopper) = this(id,name,owner,Seq.empty)

   def this(id: Long, name: String, owner: Shopper) = this(Some(id),name,owner,Seq.empty)

   def this(name: String, owner: Shopper) = this(None,name,owner,Seq.empty)

   def save(implicit registry: ComponentRegistry): Option[ShoppingList] = {
      registry.shoppingListRepository.save(this).map( newId => this.copy(id=Some(newId)) )
   }

   def findItems(implicit registry: ComponentRegistry): Seq[ShoppingItem] = for{
         listId <- id.toList
         item   <- registry.shoppingItemRepository.findItems(listId)
      } yield item

   def findItem(itemId: Long)(implicit registry: ComponentRegistry): Option[ShoppingItem] = {
      id.flatMap(registry.shoppingItemRepository.findItem(_,itemId))
   }

}

object ShoppingLists {

   def findList(listId: Long)(implicit registry: ComponentRegistry) = registry.shoppingListRepository.findList(listId)

}


