package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import play.api.mvc.Results._
import play.api.data._
import play.api.data.Forms._
import models._
import scala.concurrent.Future



class ShoppingListRequest[A](val shoppingList: ShoppingList, val request: ShopperRequest[A]) extends WrappedRequest[A](request){
	val shopper = request.shopper
}

trait ShoppingActions {

	def ShoppingListAction(listId: Long) = new ActionRefiner[ShopperRequest, ShoppingListRequest] {
		def refine[A](input: ShopperRequest[A]) = Future.successful {
			( for{
				shopper <- input.shopper
				list    <- shopper.findList(listId)
			} yield new ShoppingListRequest(list,input)
			).toRight(NotFound(views.html.index()))
		}
	}

	implicit def currentShoppingListShopper[A](implicit request: ShoppingListRequest[A]): Option[Shopper] = request.shopper

}

object ShoppingController extends Controller with Secured with ShoppingActions {

	def viewShopper(username: String) = (ShopperAction andThen AuthenticatedCheckAction) { implicit request =>
		val myLists = request.shopper.map(_.myLists).toList.flatten
		val otherLists = request.shopper.map(_.otherLists).toList.flatten
		Ok(views.html.home(myLists,otherLists))
	}

	def viewList(username: String, listId: Long) = (ShopperAction
			andThen AuthenticatedCheckAction
			andThen ShoppingListAction(listId)) { implicit request =>
		val list = request.shoppingList.includeItems
		Ok(views.html.shop.viewList(list))
	}

}
