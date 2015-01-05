package shop.model

import com.github.t3hnar.bcrypt._
// import spray.util.LoggingContext
// import akka.event.Logging
import shop.infrastructure._

// TODO change to actors

case class RegistrationDetails(username: String, password: String) { // extends Logging {
   def register(implicit registry: ComponentRegistry): Option[Shopper] = {
      Shoppers.findShopper(username) match {
         case None => {
            for{
               encryptedPassword <- Some(password.bcrypt)
               shopperId         <- registry.shopperRepository.save(username)
               _                 <- registry.identityRepository.save(shopperId,encryptedPassword)
               shopper           =  new Shopper(shopperId,username)
               _                 <- shopper.createInitialList
            } yield new Shopper(shopperId,username)
         }
         case _ => None
      }
   }
}

case class LoginDetails(username: String, password: String) { // extends Logging {
   def authenticate(implicit registry: ComponentRegistry): Option[Shopper] = {
      for{
         shopper   <- Shoppers.findShopper(username)
         shopperId <- shopper.id
         encryptedPassword  <- registry.identityRepository.findEncryptedPassword(shopperId)
         if password.isBcrypted(encryptedPassword)
      } yield shopper
   }
}
