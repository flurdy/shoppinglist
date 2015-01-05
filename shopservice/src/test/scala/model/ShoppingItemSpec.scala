package shop.model

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.mock.Mockito
import shop.service._
import shop.model._
import shop.infrastructure._

class ShoppingItemSpec extends Specification with Mockito {

   "Shopping Item model" should {

      "save" in new ScopedRegistry {
         val shopper = new Shopper("username")
         val list    = new ShoppingList(12L,"list 1",shopper)
         registry.shoppingItemRepository.save(any[ShoppingList],any[ShoppingItem]) returns Some(123L)
         new ShoppingItem("item 1").save(list) must beSome
         there was one(registry.shoppingItemRepository).save(any[ShoppingList],any[ShoppingItem])
      }

      "update" in new ScopedRegistry {
         val shopper = new Shopper("username")
         val list    = new ShoppingList(12L,"list 1",shopper)
         val item    = new ShoppingItem(1L,"item 1")
         registry.shoppingItemRepository.update(any[ShoppingItem]) returns Some(item)
         item.update must beSome
         there was one(registry.shoppingItemRepository).update(any[ShoppingItem])
      }

   }

}



