package models

import infrastructure._
import play.api.libs.json._

case class ErrorMessage(message: String)

case class LoginDetails(username: String, password: String){
   def toJson = Json.obj("username" -> username, "password" -> password)
}

case class RegisterDetails(username: String, password: String, confirmPassword: String){
	def register = ShopperAdapter.register(this)
   def toJson = Json.obj("username" -> username, "password" -> password)
}

object Authentication {

	def authenticate(loginDetails: LoginDetails): Option[Shopper] = {
		for{
         loginDetails <- ShopperAdapter.authenticate(loginDetails)
         shopper      <- Shoppers.findShopper(loginDetails.username)
      } yield shopper
	}

}
