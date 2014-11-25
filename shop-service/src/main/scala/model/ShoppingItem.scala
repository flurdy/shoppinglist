package shop.model

import shop.infrastructure._

case class ShoppingItem(id: Option[Long], name: String, description: String){

   def save: Option[ShoppingItem] = ShoppingItemRepository.save(this)

}



