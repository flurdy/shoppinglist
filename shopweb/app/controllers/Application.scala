package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import play.api.mvc.Results._
import play.api.data._
import play.api.data.Forms._
import models._
import scala.concurrent.Future


class ShopperRequest[A](val shopper: Option[Shopper], val request: Request[A]) extends WrappedRequest[A](request){
	def isAuthenticated = shopper.isDefined
}

// class WithShopperRequest[A](val shopper: Shopper, request: Request[A]) extends WrappedRequest[A](request)

object ShopperAction extends ActionBuilder[ShopperRequest] with ActionTransformer[Request, ShopperRequest] {
	def transform[A](request: Request[A]) = Future.successful {
		val shopper = for{
			username <- request.session.get("username")
			shopper  <- Shoppers.findShopper(username)
		} yield shopper
		new ShopperRequest( shopper, request)
	}
}

object AuthenticatedCheckAction extends ActionFilter[ShopperRequest] {
	def filter[A](input: ShopperRequest[A]) = Future.successful {
		if (input.shopper.isDefined) None
		else Some(Forbidden(views.html.login(Application.loginForm)))
	}
}

trait Secured {

	implicit def currentShopper[A](implicit request: ShopperRequest[A]): Option[Shopper] = {
		request.shopper
	}

	// def WithShopperAction = new ActionRefiner[ShopperRequest,WithShopperRequest]{
	// 	def refine[A](input: ShopperRequest[A]) = Future.successful {
	// 		input.shopper.map(new WithShopperRequest(_,input.request)).toRight(NotFound)
	// 	}
	// }

}


object Application extends Controller with Secured {

	def index = ShopperAction { request =>
      request.shopper match {
         case Some(shopper) if request.isAuthenticated => {
			   Redirect(routes.ShoppingController.viewShopper(shopper.username))
         }
         case _ => Ok(views.html.index())
		}
	}

	def about = ShopperAction { implicit request =>
		Ok(views.html.about())
	}

	def help = ShopperAction { implicit request =>
      Ok(views.html.about())
   }

	def contact = ShopperAction { implicit request =>
      Ok(views.html.about())
   }

	val registerFields = mapping (
		"username" -> text,
		"password" -> text,
		"confirmPassword" -> text
	)(RegisterDetails.apply)(RegisterDetails.unapply) verifying("Passwords does not match", fields => fields match {
		case registerDetails => registerDetails.password == registerDetails.confirmPassword
	})

	val registerForm = Form( registerFields )

	def viewRegister = ShopperAction { implicit request =>
		Ok(views.html.register(registerForm))
	}

	def register = Action { implicit request =>
		registerForm.bindFromRequest.fold(
			errors => {
				Logger.warn("Registration form error")
				BadRequest(views.html.register(errors))
			},
			registerDetails => {
				Shoppers.findShopper(registerDetails.username) match {
					case Some(shopper) => {
						Logger.warn(s"Registration failed. Username taken: ${registerDetails.username}")
						implicit val errorMessages = List(ErrorMessage("Registration failed. The username is already taken"))
						BadRequest(views.html.register(registerForm.fill(registerDetails)))
					}
					case None => {
						registerDetails.register match {
							case Some(shopper) => {
								Logger.warn(s"Registered: ${registerDetails.username}")
								Redirect(routes.Application.index).withSession("username" -> registerDetails.username)
							}
							case None => {
								Logger.warn(s"Registration failed: ${registerDetails.username}")
								implicit val errorMessages = List(ErrorMessage("Registration failed"))
								BadRequest(views.html.register(registerForm.fill(registerDetails)))
							}
						}
					}
				}
			}
    	)
	}

	val loginFields = mapping(
		"username" -> text,
		"password" -> text
	)(LoginDetails.apply)(LoginDetails.unapply)

	val loginForm = Form( loginFields )

	def viewLogin = ShopperAction { implicit request =>
		Ok(views.html.login(loginForm))
	}

	def login = Action { implicit request =>
		loginForm.bindFromRequest.fold(
			errors => {
				Logger.warn("Login form error")
				BadRequest(views.html.login(errors))
			},
			loginDetails => {
				loginDetails.authenticate match {
					case Some(shopper) => {
						Redirect(routes.Application.index).withSession("username" -> loginDetails.username)
					}
					case None => {
						Logger.warn(s"Authentication failed for ${loginDetails.username}")
						implicit val errorMessages = List(ErrorMessage(
							"Authentication failed. Either the user does not exist or the password is incorrect"))
						BadRequest(views.html.login(loginForm.fill(loginDetails)))
					}
				}
			}
    	)
	}

	def logout = ShopperAction { implicit request =>
		Redirect(routes.Application.index).withNewSession
	}

}
