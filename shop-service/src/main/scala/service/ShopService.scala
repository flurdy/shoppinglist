package shop.service

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.http.HttpHeaders._
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._
import HttpCharsets._
import MediaTypes._
import shop.model._
import spray.util._
import akka.event.Logging


class ShopServiceActor extends Actor with ShopService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

object ShopJsonProtocol extends DefaultJsonProtocol {
  implicit val registerFormat     = jsonFormat2(RegistrationDetails)
  implicit val authenticateFormat = jsonFormat2(LoginDetails)
  implicit val shopperFormat      = jsonFormat2(Shopper)
}

import ShopJsonProtocol._

trait ShopService extends HttpService {

  val log = LoggingContext.fromActorRefFactory

  val myRoute =
    path("heartbeat") {
      get {
        complete {
          <h1>ALIVE!</h1>
        }
      }
    } ~
    path("register") {
      post {
        entity(as[RegistrationDetails]){ details =>
          detach(){
            rejectEmptyResponse {
              respondWithStatus(201) {
                respondWithMediaType(MediaTypes.`application/json`){
                  log.info(s"Registering ${details.username}")
                  val shopper = details.register
                  log.debug("Registered: "+shopper.isDefined)
                  val id = shopper.flatMap( s => s.id.map ( i => i ) ).getOrElse(-1)
                  respondWithHeader(RawHeader("Location", s"/shopper/${id}")) {
                    complete(shopper)
                  }
                }
              }
            }
          }
        }
      }
    } ~
    path("authenticate") {
      post {
        rejectEmptyResponse {
          respondWithMediaType(MediaTypes.`application/json`){
            entity(as[LoginDetails]){ details =>    
              log.info(s"Authenticating ${details.username}")
              val shopper = details.authenticate
              log.info("Authenticated: "+shopper.isDefined)
              complete(shopper)
            }
          }
        }
      }
    } ~
    pathPrefix("shopper" / Segment) { username =>
      pathEnd {
        rejectEmptyResponse {
          respondWithMediaType(MediaTypes.`application/json`){
            log.info(s"Looking for shopper $username")
            val shopper = Shoppers.findShopper(username)
            log.info("Shopper found: "+shopper.isDefined)
            complete(shopper)
          }
        }
      } ~
      path("lists") {
        pathEnd {
          get {
            complete{
"""[
  {
    "id": 123,
    "owner": {
      "id": 456,
      "username": "blaah"
    }
    "name": "christmas"
  },
  {
    "id": 456,
    "owner": {
      "id": 456,
      "username": "blaah"
    }
    "name": "groceries"
  }
]
"""
            }          
          }
        } ~ 
        path("other") {
          get {
            complete{
"""[
  {
    "id": 123,
    "owner": {
      "id": 456,
      "username": "blaah"
    }
    "name": "christmas"
  },
  {
    "id": 456,
    "owner": {
      "id": 456,
      "username": "blaah"
    }
    "name": "groceries"
  }
]
""" 
            }
          }          
        }
      } ~ 
      path("list") {
        pathEnd {
          post {
            complete{
"""{
  "id": 123,
  "owner": {
    "id": 456,
    "username": "blaah"
  }
  "name": "christmas"
}
"""       
            }
          }
        } ~
        pathPrefix( IntNumber ) { listId =>
          pathEnd {
            (put | parameter('method ! "put")) {
              complete {
"""{
  "id": 123,
  "owner": {
    "id": 456,
    "username": "blaah"
  }
  "name": "christmas"
}
"""             
              }
            } ~
            get{
              val shoppingList = None
              complete{
"""{
  "id": 123,
  "owner": {
    "id": 456,
    "username": "blaah"
  }
  "name": "christmas"
}
"""         
              }
            }
          } ~ 
          path("items"){
            pathEnd {
              get{
                complete{
"""[
  {
    "id": 123,
    "name": "boot"
  },
  {
    "id": 345,
    "name": "bicycle"
  }
]
"""            
                } 
              }
            }
          } ~
          path("item"){
            pathEnd {
              post {
                complete{
  """{
    "id": 345,
    "name": "bicycle"
  }
  """             
                }
              }
            }
            pathPrefix( IntNumber ) { listId =>
              pathEnd {
                (put | parameter('method ! "put")) {
                  complete {
  """{
    "id": 345,
    "name": "bicycle"
  }
  """ 
                  }
                } ~
                get {
                  complete{
  """{
    "id": 345,
    "name": "bicycle"
  }
  """             
                  }
                }
              }
            }
          }
        }
      } 
    } 

}
