package shop.repository

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.mock.Mockito
import shop.service._
import shop.model._
import shop.infrastructure._
import org.h2.jdbc.JdbcSQLException


class IdentityRepositorySpec extends Specification with RuntimeRegistry with Repository {

   val identityRepository = registry.identityRepository
   val shopperRepository  = registry.shopperRepository

   "Identity repository" should {

      "save shopper identity" in {
         val identityId = identityRepository.save(123L, "password")
         identityId must beSome(beGreaterThan(0L))
      }

      "save several identities" in {
         val identityId1 = identityRepository.save(124L, "password")
         val identityId2 = identityRepository.save(125L, "password")
         identityId1 must beSome(beGreaterThan(0L))
         identityId2 must beSome(beGreaterThan(0L))
         identityId1 must not be equalTo(identityId2)
      }

      "not find password" in {
         val password = identityRepository.findEncryptedPassword(123456L)
         password must beNone
      }

      "find password" in new CleanDatabase {
         val password = for {
            shopperId  <- shopperRepository.save("usernamer")
            identityId <- identityRepository.save(shopperId, "password")
            password   <- identityRepository.findEncryptedPassword(shopperId)
         } yield password
         password must beSome(beEqualTo("password"))
      }

   }

}

class ShopperRepositorySpec extends Specification with RuntimeRegistry {

   val shopperRepository = registry.shopperRepository

   "ShopperRepository" should {

      "save a shopper" in {
         val shopperId = shopperRepository.save("username")
         shopperId must beSome(beGreaterThan(0L))
      }

      "save several shoppers" in {
         val shopperId1 = shopperRepository.save("username2")
         val shopperId2 = shopperRepository.save("username3")
         shopperId1 must beSome(beGreaterThan(0L))
         shopperId2 must beSome(beGreaterThan(0L))
         shopperId1 must not be equalTo(shopperId2)
      }

      "not save an existing shopper" in {
         shopperRepository.save("username4")
         shopperRepository.save("username4") must throwA[JdbcSQLException]
      }

      "find no shopper" in {
         val shopper = shopperRepository.findShopper("testuser")
         shopper must beNone
      }

      "find a shopper by username" in {
         shopperRepository.save("username5")
         val shopper = shopperRepository.findShopper("username")
         shopper must beSome
      }

      "find a shopper by id " in {
         val shopper = for{
            shopperId <- shopperRepository.save("username43s")
            shopper   <- shopperRepository.findShopperById(shopperId)
            } yield shopper
         shopper must beSome
      }

   }
}
