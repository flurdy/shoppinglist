package infrastructure

import models._
import play.api.Logger
import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.{Future,Promise,Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object ShoppingListAdapter extends ServiceAdapter {

   private def findListsUrl(shopper: Shopper) = s"$serviceContextUrl/shopper/${shopper.username}/shoppinglist/"

   private def findOtherListsUrl(shopper: Shopper) = s"$serviceContextUrl/shopper/${shopper.username}/shoppinglist/other/"
   
   private def findListUrl(username: String, listId: Long) = s"$serviceContextUrl/shopper/${username}/shoppinglist/$listId/"
   
   private def findItemsUrl(username: String, listId: Long) = s"$serviceContextUrl/shopper/${username}/shoppinglist/$listId/items/"

	def findMyLists(shopper: Shopper): Seq[ShoppingList] = {   
      val lists = findMyListsCall(shopper)
      Await.result(lists, timeoutCall)
	}

   private def findMyListsCall(shopper: Shopper): Future[Seq[ShoppingList]] = {    
      WS.url(findListsUrl(shopper)).withRequestTimeout(timeoutHttp).get() map { response =>
         if(response.status == 200) ShoppingLists.parseLists(response.body)
         else Seq.empty
      }
   }

	def findOtherLists(shopper: Shopper): Seq[ShoppingList] = {
      val lists = findOtherListsCall(shopper)
      Await.result(lists, timeoutCall)
	}

   private def findOtherListsCall(shopper: Shopper): Future[Seq[ShoppingList]] = {    
      WS.url(findOtherListsUrl(shopper)).withRequestTimeout(timeoutHttp).get() map { response =>
         if(response.status == 200) ShoppingLists.parseLists(response.body)
         else Seq.empty
      }
   }

	def findList(username: String, listId: Long): Option[ShoppingList] = {
      val lists = findListCall(username,listId)
      Await.result(lists, timeoutCall)
	}
   
   private def findListCall(username: String, listId: Long): Future[Option[ShoppingList]] = {    
      WS.url(findListUrl(username,listId)).withRequestTimeout(timeoutHttp).get() map { response =>
         if(response.status == 200) ShoppingLists.parseList(response.body)
         else None
      }
   }

   def listIsAccessibleByShopper(username: String, listId: Long): Boolean = {
      findList(username,listId).isDefined
   }

   def findShoppingItems(username: String, list: ShoppingList): Seq[ShoppingItem] = {
      val items = findItemsCall(username,list.id.get)
      Await.result(items, timeoutCall)
   }

   private def findItemsCall(username: String, listId: Long): Future[Seq[ShoppingItem]] = {    
      WS.url(findItemsUrl(username,listId)).withRequestTimeout(timeoutHttp).get() map { response =>
         if(response.status == 200) ShoppingItems.parseItems(response.body)
         else Seq.empty
      }
   }

}
