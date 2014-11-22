package infrastructure

import models._
import play.api.Logger
import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.{Future,Promise,Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object ShopperAdapter {

  	implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

   val timeoutHttp = 5000
   val timeoutCall = 6 seconds

	val registerUrl 	  = "http://localhost:8080/register"
	val authenticateUrl = "http://localhost:8080/authenticate"
	
	def findShopperUrl(username: String) = s"http://localhost:8080/shopper/$username"

	def register(registerDetails: RegisterDetails): Option[RegisterDetails] = {	
		val registered = registerCall(registerDetails)
		Await.result(registered, timeoutCall)
	}	

	private def registerCall(registerDetails: RegisterDetails): Future[Option[RegisterDetails]] = {		
      WS.url(registerUrl).withRequestTimeout(timeoutHttp).post(registerDetails.toJson) map { response =>
      	if(response.status == 201) Some(registerDetails)
      	else None
      }
	}

	def authenticate(loginDetails: LoginDetails): Option[LoginDetails] = {		
		val response = authenticateCall(loginDetails)
		Await.result(response, timeoutCall)
	}

	private def authenticateCall(loginDetails: LoginDetails): Future[Option[LoginDetails]] = {	
      WS.url(authenticateUrl).withRequestTimeout(timeoutHttp).post(loginDetails.toJson) map { response =>
      	if(response.status == 200) Some(loginDetails)
      	else None
      }
	}

	def findShopper(username: String): Option[Shopper] = {
		val response = findShopperCall(username)
		Await.result(response, timeoutCall)
	}

	private def findShopperCall(username: String): Future[Option[Shopper]] = {
      WS.url(findShopperUrl(username)).withRequestTimeout(timeoutHttp).get() map { response =>
      	if(response.status == 200) Shoppers.fromJson(response.body)
      	else None
      }
	}

}
