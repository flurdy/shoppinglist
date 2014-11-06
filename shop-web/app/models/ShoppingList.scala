package models

import infrastructure._


case class ShoppingList(id: Option[Long], name: String, owner: Shopper, items: Seq[ShoppingItem]){

   def this(id: Option[Long], name: String, owner: Shopper) = this(id, name, owner, Seq.empty)

   def includeItems = this.copy(items = ShoppingListAdapter.findShoppingItems(this))

   def isAccessibleBy(shopper: Shopper) = {
      ( for{
         listId    <- id
         shopperId <- shopper.id
      } yield ShoppingListAdapter.listIsAccessibleByShopper(listId,shopperId)
      ).getOrElse(false)
   }


}

object ShoppingLists {

	def findList(listId: Long): Option[ShoppingList] = ShoppingListAdapter.findList(listId)

}
