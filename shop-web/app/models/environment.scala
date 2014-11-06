package models

import infrastructure._


case class ErrorMessage(message: String)

case class LoginDetails(username: String, password: String)

case class RegisterDetails(username: String, password: String, confirmPassword: String){
	def register = ShopperAdapter.register(this)
}

object Authentication {

	def authenticate(loginDetails: LoginDetails): Option[Shopper] = {
		ShopperAdapter.authenticate(loginDetails)
	}

}
