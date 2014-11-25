package shop.infrastructure

import shop.model._


object IdentityRepository {

   def save(username: String, password: String): Long = {
      // TODO
      (Math.random*1000).toInt
   }

   def findEncryptedPassword(username: String): Option[String] = {
      // TODO
      Some("123")
      // None
   }
   
}


object ShopperRepository {

   def findShopper(username: String): Option[Shopper] = {
      // TODO
      if(username == "testuser") Some(Shopper(Some(123L),username))
      else None
   }

}

object ShoppingListRepository {

   // TODO
   def findOwnerLists(shopperId: Long): Seq[ShoppingList] = Seq.empty

   // TODO
   def findList(listId: Long): Option[ShoppingList] = None

   // TODO
   def findItems(listId: Long): Seq[ShoppingItem] = Seq.empty

   // TODO
   def save(list: ShoppingList): Option[ShoppingList] = Some(list)

}

object ShoppingItemRepository {

   // TODO
   def findItem(listId: Long, itemId: Long): Option[ShoppingItem] = None

   // TODO
   def save(item: ShoppingItem): Option[ShoppingItem] = Some(item)

}
