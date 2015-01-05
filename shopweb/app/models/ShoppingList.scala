package models

import infrastructure._
import play.api.libs.json._


case class ShoppingList(id: Option[Long], name: String, owner: Shopper, items: Seq[ShoppingItem]){

   def this(id: Option[Long], name: String, owner: Shopper) = this(id, name, owner, Seq.empty)

   def includeItems(username: String) = this.copy(items = ShoppingListAdapter.findShoppingItems(username,this))

   def isAccessibleBy(shopper: Shopper) = {
      id.map( ShoppingListAdapter.listIsAccessibleByShopper(shopper.username,_) ).getOrElse(false)
   }

}

object ShoppingLists {

	def findList(shopper: Shopper, listId: Long): Option[ShoppingList] = ShoppingListAdapter.findList(shopper.username, listId)

   def parseLists(json: String): Seq[ShoppingList] = {
      val jsonArray: JsArray  = Json.arr(Json.parse(json))
      for{
         listJson <- jsonArray.value
         list     <- parseListJson(listJson)
      } yield list
   }

   def parseList(json: String): Option[ShoppingList] = parseListJson(Json.parse(json))

   def parseListJson(json: JsValue): Option[ShoppingList] = {
      for {
         id       <- (json \ "id") .asOpt[Long]
         name     <- (json \ "name") .asOpt[String]
         username <- (json \ "owner" \ "username") .asOpt[String]
         items    =  Seq.empty
      } yield ShoppingList(Some(id), name, new Shopper(username), items)
   }

}
