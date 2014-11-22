package shop.infrastructure

import shop.model._


object IdentityRepository {

   def save(username: String, password: String): Long = {
      // TODO
      (Math.random*1000).toInt
   }

   def findEncryptedPassword(username: String): Option[String] = {
      // TODO
      None
   }
   
}


object ShopperRepository {

   def findShopper(username: String): Option[Shopper] = {
      // TODO
      if(username == "testuser") Some(Shopper(Some(123L),username))
      else None
   }

}

