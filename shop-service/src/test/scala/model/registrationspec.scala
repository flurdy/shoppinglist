package shop.model

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.mock.Mockito
import shop.service._
import shop.model._
import shop.infrastructure._


class LoginDetailsSpec extends Specification with Mockito {

   "Login details" should {

      "authenticate" in new ScopedRegistry {
         registry.shopperRepository.findShopper(anyString) returns Some(Shopper(Some(123L),"username1"))
         registry.identityRepository.findEncryptedPassword(anyLong) returns Some("$2a$10$QgAVJoX82OB3H5vyQ19zhOe5JoahY0tp9Sae1y6pZGJ0j3UhO7erm")
         LoginDetails("username1","password").authenticate must beSome
         there was one(registry.identityRepository).findEncryptedPassword(anyLong)
      }

      "not authenticate with wrong password" in new ScopedRegistry {
         registry.shopperRepository.findShopper("username2") returns Some(Shopper(Some(456L),"username2"))
         registry.identityRepository.findEncryptedPassword(anyLong) returns Some("$2a$10$TDpoAdhVjIyti123/123TebTk4NL9iTZKAn2ua8PZRWrEBj6VsTFC")
         LoginDetails("username2","password").authenticate must beNone
         there was one(registry.identityRepository).findEncryptedPassword(anyLong)
      }

      "not authenticate with unknown shopper" in new ScopedRegistry{
         registry.shopperRepository.findShopper(anyString) returns None
         LoginDetails("username3","password").authenticate must beNone
      }

   }

}


class RegistrationDetailsSpec extends Specification with Mockito {

   "Registration details" should {

      "register" in new ScopedRegistry {
         registry.shopperRepository.findShopper(anyString) returns None
         registry.shopperRepository.save(anyString) returns Some(123L)
         registry.identityRepository.save(anyLong,anyString) returns Some(456L)
         RegistrationDetails("username","password").register must beSome
         there was one(registry.shopperRepository).save(anyString)
         there was one(registry.identityRepository).save(anyLong,anyString)
      }

      "not register existing usernames" in new ScopedRegistry {
         registry.shopperRepository.findShopper(anyString) returns Some(Shopper(Some(123L),"username"))
         RegistrationDetails("username","password").register must beNone
         there was no(registry.shopperRepository).save(anyString)
         there was no(registry.identityRepository).save(anyLong,anyString)
      }

   }
}


