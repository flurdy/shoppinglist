package models

case class ErrorMessage(message: String)

case class LoginDetails(username: String, password: String)

case class RegisterDetails(username: String, password: String, confirmPassword: String){
	def register {
		// todo
	}
}

object Authentication {

	def authenticate(loginDetails: LoginDetails): Option[Shopper] = {
		Some(Shopper(None,loginDetails.username))
	}

}
