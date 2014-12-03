package shop

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import shop.service._
import shop.infrastructure._
import shop.model._

object Boot extends App {

  implicit val system = ActorSystem("shop-service-system")

  implicit val componentRegistry = new RuntimeComponentRegistry

  val service = system.actorOf(Props(classOf[ShopServiceActor],componentRegistry), "shop-service")

  implicit val timeout = Timeout(5.seconds)

  (new RepositoryInitialiser).initialiseDatabase

  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8880)

}
