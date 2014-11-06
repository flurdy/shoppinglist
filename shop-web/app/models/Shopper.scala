package models

import play.api.Logger
import infrastructure._


case class Shopper(id: Option[Long], username: String){

	def myLists: Seq[ShoppingList] = ShoppingListAdapter.findMyLists(this)

	def otherLists: Seq[ShoppingList] = ShoppingListAdapter.findOtherLists(this)

	def findList(listId: Long): Option[ShoppingList] = {
		for{
			list <- ShoppingListAdapter.findList(listId) if list.isAccessibleBy(this)
		} yield list
	}
}

object Shoppers {

	def findShopper(username: String): Option[Shopper] = {
		ShopperAdapter.findShopper(username)
	}

}
