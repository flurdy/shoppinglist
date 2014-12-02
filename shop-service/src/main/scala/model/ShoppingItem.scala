package shop.model

import shop.infrastructure._

case class ShoppingItem(id: Option[Long], name: String, description: Option[String]){

   def this(id: Long,name: String) = this(Some(id),name,None)

   def this(name: String)          = this(None,name,None)

   def update(implicit registry: ComponentRegistry): Option[ShoppingItem] = {
      registry.shoppingItemRepository.update(this)
   }

   def save(list: ShoppingList)(implicit registry: ComponentRegistry): Option[ShoppingItem] = {
      registry.shoppingItemRepository.save(list,this).map( newId => this.copy(id=Some(newId)) )
   }

   private def findList(implicit registry: ComponentRegistry): Option[ShoppingList] = {
      id.flatMap(registry.shoppingListRepository.findItemList(_))
   }

}



