package models

import play.api.Logger
import infrastructure._
import play.api.libs.json._


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

	def fromJson(shopperJson: String): Option[Shopper] = fromJsonElement(Json.parse(shopperJson))
		
	private def fromJsonElement(shopperJson: JsValue): Option[Shopper] = {
		for{
			id       <- (shopperJson \ "shopper" \ "id") .asOpt[Long]
			username <- (shopperJson \ "shopper" \ "username") .asOpt[String]
		} yield Shopper(Some(id),username)
	}

}
