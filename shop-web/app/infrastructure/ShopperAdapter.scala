package infrastructure

import models._


object ShopperAdapter {

	def register(registerDetails: RegisterDetails): Option[RegisterDetails] = {
		// TODO
		Some(registerDetails)
	}

	def authenticate(loginDetails: LoginDetails): Option[Shopper] = {
		// TODO
		Some(Shopper(Some(2),loginDetails.username))
	}

	def findShopper(username: String): Option[Shopper] = {
		// TODO
		Some(Shopper(Some(1),"flurdy"))
	}

}
