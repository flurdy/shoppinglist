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


class ShopServiceActor(val registry: ComponentRegistry) extends Actor with ShopService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

object ShopJsonProtocol extends DefaultJsonProtocol {
  implicit val registerFormat     = jsonFormat2(RegistrationDetails)
  implicit val authenticateFormat = jsonFormat2(LoginDetails)
  implicit val shopperFormat      = jsonFormat2(Shopper)
  implicit val shoppingItemFormat = jsonFormat3(ShoppingItem)
  implicit val shoppingListFormat = jsonFormat4(ShoppingList)
}

import ShopJsonProtocol._

trait ShopService extends HttpService {

  implicit val registry: ComponentRegistry

  val log = LoggingContext.fromActorRefFactory

  val myRoute =
    path("heartbeat") {
      get {
        complete {
          <h1>ALIVE!</h1>
        }
      }
    } ~
    respondWithMediaType(MediaTypes.`application/json`){
      path("register") {
        post {
          entity(as[RegistrationDetails]){ details =>
            detach(){
              rejectEmptyResponse {
                respondWithStatus(201) {
                  log.info(s"Registering ${details.username}")
                  val shopper = details.register
                  val id = shopper.flatMap( s => s.id ).getOrElse(-1)
                  respondWithHeader(RawHeader("Location", s"/shopper/${id}")) {
                    complete(shopper)
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
            entity(as[LoginDetails]){ details =>
              log.info(s"Authenticating ${details.username}")
              val shopper = details.authenticate
              complete(shopper)
            }
          }
        }
      } ~
      pathPrefix("shopper" / Segment) { username =>
        val shopper: Option[Shopper] = Shoppers.findShopper(username)
        pathEnd {
          rejectEmptyResponse {
            complete(shopper)
          }
        } ~
        path("lists") {
          pathEnd {
            get {
              rejectEmptyResponse {
                val lists: Option[Stream[ShoppingList]] = shopper.map(_.findLists.toStream)
                complete(lists)
              }
            }
          } ~
          path("other") {
            get {
              rejectEmptyResponse {
                val lists = shopper.map(_.findOtherLists)
                complete(lists.map(_.toStream))
              }
            }
          }
        } ~
        path("list") {
          pathEnd {
            post {
              rejectEmptyResponse {
                entity(as[ShoppingList]){ list =>
                  respondWithStatus(201) {
                    val shoppingList = list.save
                    val id = shoppingList.flatMap( s => s.id ).getOrElse(-1)
                    respondWithHeader(RawHeader("Location", s"/shopper/${username}/list/${id}")) {
                      complete(shoppingList)
                    }
                  }
                }
              }
            }
          } ~
          pathPrefix( IntNumber ) { listId =>
            val shoppingList = ShoppingLists.findList(listId)
            pathEnd {
              (put | parameter('method ! "put")) {
                rejectEmptyResponse {
                  entity(as[ShoppingList]){ list =>
                    val updatedList = shoppingList.flatMap(_.save)
                    complete(updatedList)
                  }
                }
              } ~
              get{
                rejectEmptyResponse {
                  complete(shoppingList)
                }
              }
            } ~
            path("items"){
              pathEnd {
                get{
                  rejectEmptyResponse {
                    val items: Option[Seq[ShoppingItem]] = shoppingList.map(_.findItems)
                    complete(items.map(_.toStream))
                  }
                }
              }
            } ~
            path("item"){
              pathEnd {
                post {
                  rejectEmptyResponse {
                    entity(as[ShoppingItem]){ item =>
                      respondWithStatus(201) {
                        val id = shoppingList.
                          flatMap( list => item.save(list)).
                          flatMap( s => s.id ).getOrElse(-1)
                        respondWithHeader(RawHeader("Location", s"/shopper/${username}/list/${listId}/item/${id}")) {
                          complete(item)
                        }
                      }
                    }
                  }
                }
              } ~
              pathPrefix( IntNumber ) { itemId =>
                val shoppingItem = shoppingList.flatMap( _.findItem(itemId) )
                pathEnd {
                  (put | parameter('method ! "put")) {
                    rejectEmptyResponse {
                      entity(as[ShoppingItem]){ item =>
                        val updatedItem = item.update
                        complete(updatedItem)
                      }
                    }
                  } ~
                  get {
                    rejectEmptyResponse {
                      complete(shoppingItem)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

}
