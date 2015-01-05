package infrastructure

import models._
import play.api.Logger
import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.{Future,Promise,Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object IdentityAdapter extends ServiceAdapter {

   private val registerUrl     = s"$serviceContextUrl/register"
   private val authenticateUrl = s"$serviceContextUrl/authenticate"

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

}
