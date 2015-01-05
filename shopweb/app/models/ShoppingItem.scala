package models

import play.api.libs.json._


case class ShoppingItem(id: Option[Long], name: String, description: String){
   
   def this(id: Long, name: String) = this(Some(id),name,"")

}


object ShoppingItems {
   
   def parseItems(json: String): Seq[ShoppingItem] = parseItemsJson(Json.parse(json))

   def parseItemsJson(json: JsValue) = {
      val jsonArray: JsArray  = Json.arr(json)
      for{
         itemJson <- jsonArray.value
         item     <- parseItemJson(itemJson)
      } yield item
   }

   def parseItemJson(json: JsValue) = {
      for {
         id       <- (json \ "id") .asOpt[Long]
         name     <- (json \ "name") .asOpt[String]
      } yield new ShoppingItem(id, name)
   }

}
