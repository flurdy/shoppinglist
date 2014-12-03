package models

import infrastructure._
import play.api.libs.json._
import play.api.Play
import play.api.Play.current

case class ErrorMessage(message: String)

case class LoginDetails(username: String, password: String){

   def toJson = Json.obj("username" -> username, "password" -> password)

   def authenticate: Option[Shopper] = {
      for{
         loginDetails <- IdentityAdapter.authenticate(this)
         shopper      <- Shoppers.findShopper(username)
      } yield shopper
   }

}

case class RegisterDetails(username: String, password: String, confirmPassword: String){

   def register = IdentityAdapter.register(this)

   def toJson = Json.obj("username" -> username, "password" -> password)

}

object Environment {

   def serviceContextUrl: String = Play.configuration.getString("shop.service.url").getOrElse("http://localhost")

}
