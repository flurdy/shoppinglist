package infrastructure

import models._


object ShoppingListAdapter {

	def findMyLists(shopper: Shopper): Seq[ShoppingList] = {
		// TODO
		Seq( new ShoppingList(Some(1),"Weekly list",Shopper(None,"flurdy")) )
	}

	def findOtherLists(shopper: Shopper): Seq[ShoppingList] = {
		// TODO
		Seq( new ShoppingList(Some(2),"travel shopping",Shopper(None,"susan")) )
	}

	def findList(listId: Long): Option[ShoppingList] = {
		// TODO
		Some( new ShoppingList(Some(2),"travel shopping",Shopper(None,"susan")) )
	}

   def listIsAccessibleByShopper(listId: Long, shopperId: Long) = {
      // TODO
      true
   }

   def findShoppingItems(list: ShoppingList): Seq[ShoppingItem] = {
      // TODO
      Seq( ShoppingItem(Some(2),"wall clock",""), ShoppingItem(Some(2),"porsche","911 in red") )
   }

}
