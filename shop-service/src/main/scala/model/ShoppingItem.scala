package shop.model

import shop.infrastructure._

case class ShoppingItem(id: Option[Long], name: String, description: Option[String]){

   def this(id: Long,name: String) = this(Some(id),name,None)

   def save: Option[ShoppingItem] = {
      for {
         list  <- findList
         newId <- ShoppingItemRepository.save(list,this)
      } yield this.copy(id=Some(newId))
   }

   private def findList: Option[ShoppingList] = id.flatMap(ShoppingListRepository.findItemList(_))

}



