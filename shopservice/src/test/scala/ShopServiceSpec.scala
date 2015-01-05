package shop.service

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import shop.model._

class ShopServiceSpec extends Specification with Specs2RouteTest with ShopService with TestRegistry {
  def actorRefFactory = system

  "ShopService" should {

    "return a greeting for GET requests to the heartbeat" in {
      Get("/heartbeat") ~> myRoute ~> check {
        responseAs[String] must contain("ALIVE")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put("/heartbeat") ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }


    // "return a Not found error for non existant entity" in {
    //   Put("/shopper/123/list/-1") ~> sealRoute(myRoute) ~> check {
    //     status === NotFound
    //     responseAs[String] === "HTTP method not allowed, supported methods: GET"
    //   }
    // }

  }

}
