package models

import play.api.Logger
import infrastructure._
import play.api.libs.json._


case class Shopper(id: Option[Long], username: String){

	def this(username: String) = this(None,username)

	def myLists: Seq[ShoppingList] = ShoppingListAdapter.findMyLists(this)

	def otherLists: Seq[ShoppingList] = ShoppingListAdapter.findOtherLists(this)

	def findList(listId: Long): Option[ShoppingList] = {
		for{
			list <- ShoppingListAdapter.findList(username,listId) if list.isAccessibleBy(this)
		} yield list
	}
}

object Shoppers {

	def findShopper = ShopperAdapter.findShopper _
	
	def parseShopper(shopperJson: String): Option[Shopper] = parseShopperJson(Json.parse(shopperJson))
		
	private def parseShopperJson(json: JsValue): Option[Shopper] = {
		for{
			id       <- (json \ "id") .asOpt[Long]
			username <- (json \ "username") .asOpt[String]
		} yield Shopper(Some(id),username)
	}

}
