package infrastructure

import models._
import play.api.Logger
import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.{Future,Promise,Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait ServiceAdapter {

  	implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

   val timeoutHttp = 5000
   val timeoutCall = 6 seconds

   val serviceContextUrl = "http://localhost:8080"

}

object ShopperAdapter extends ServiceAdapter {
	
	def findShopperUrl(username: String) = s"http://localhost:8080/shopper/$username"

	def findShopper(username: String): Option[Shopper] = {
		val response = findShopperCall(username)
		Await.result(response, timeoutCall)
	}

	private def findShopperCall(username: String): Future[Option[Shopper]] = {
      WS.url(findShopperUrl(username)).withRequestTimeout(timeoutHttp).get() map { response =>
      	if(response.status == 200) Shoppers.parseShopper(response.body)
      	else None
      }
	}

}
