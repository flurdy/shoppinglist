package shop.model

import com.github.t3hnar.bcrypt._
import spray.util.LoggingContext
import akka.event.Logging
import shop.infrastructure._

// TODO change to actors

case class RegistrationDetails(username: String, password: String){
   
   def register: Option[Shopper] = {
      Shoppers.findShopper(username) match {
         case None => {
            val encryptedPassword = password.bcrypt
            val shopperId = IdentityRepository.save(username,encryptedPassword)
            Some(Shopper(Some(shopperId),username))
         }
         case _ => None
      }
   }

}

case class LoginDetails(username: String, password: String){
   def authenticate: Option[Shopper] = {
      for{
         shopper  <- Shoppers.findShopper(username)
         password <- IdentityRepository.findEncryptedPassword(username)
         if password.isBcrypted(password)
      } yield shopper
   }

}

