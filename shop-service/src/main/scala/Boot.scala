package shop

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import shop.service._

object Boot extends App {

  implicit val system = ActorSystem("shop-service-system")

  val service = system.actorOf(Props[ShopServiceActor], "shop-service")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

}
