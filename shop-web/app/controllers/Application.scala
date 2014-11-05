package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import play.api.mvc.Results._
import play.api.data._
import play.api.data.Forms._
import models._
import scala.concurrent.Future

// def ShoppingItemAction(itemId: String) = new ActionRefiner[ShopperRequest, ShopperItemRequest] {
//   def refine[A](input: ShopperRequest[A]) = Future.successful {
//     ShoppingItem.findById(itemId)
//       .map(new ShoppingItemRequest(_, input))
//       .toRight(NotFound)
//   }
// }

class ShopperRequest[A](val shopper: Option[Shopper], request: Request[A]) extends WrappedRequest[A](request){
	def isAuthenticated = shopper.isDefined
}

object ShopperAction extends ActionBuilder[ShopperRequest] with ActionTransformer[Request, ShopperRequest] {
	def transform[A](request: Request[A]) = Future.successful {
		request.session.get("username") match {
			case Some(username) => {	
				Logger.debug("Logged in")
				Shoppers.findShopper(username) match {
					case Some(shopper) => new ShopperRequest( Some(shopper), request)
					case None 			 => throw new IllegalStateException(s"No shopper found for username: $username")
				}
			}
			case None => new ShopperRequest(None, request)			
		}
	}
}

object AuthenticatedCheckAction extends ActionFilter[ShopperRequest] {
	def filter[A](input: ShopperRequest[A]) = Future.successful {
		if (input.shopper.isDefined){ 
			Logger.debug("is logged in")
			None
		} else {
			Logger.debug("is not logged in")
			Some(Forbidden)
		}
	}
}

trait Secured {

	implicit def currentShopper[A](implicit request: ShopperRequest[A]): Option[Shopper] = {
		Logger.debug("getting current shopper")        
		request.shopper
	}

}


object Application extends Controller with Secured {

	def index = ShopperAction { request => 
		if(request.isAuthenticated) {
			Redirect(routes.Application.home)
		} else {
			Ok(views.html.index())
		}		
	}

	def home = (ShopperAction andThen AuthenticatedCheckAction) { implicit request =>
		Ok(views.html.home())
	}

	def about = ShopperAction { implicit request =>
		Ok(views.html.about())
	}

	def help = TODO

	def contact = TODO

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
						registerDetails.register
						Redirect(routes.Application.home).withSession("username" -> registerDetails.username)						
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
				Authentication.authenticate(loginDetails) match {
					case Some(shopper) => {
						Redirect(routes.Application.home).withSession("username" -> loginDetails.username)
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
