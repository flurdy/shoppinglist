package shop.model

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.mock.Mockito
import shop.service._
import shop.model._
import shop.infrastructure._

class ShoppingListSpec extends Specification with Mockito {

   "ShoppingList model" should {

      "save" in new ScopedRegistry {
         registry.shoppingListRepository.save(any[ShoppingList]) returns Some(123L)
         val shopper = new Shopper("username")
         new ShoppingList(12L,"list 1",shopper).save must beSome
         there was one(registry.shoppingListRepository).save(any[ShoppingList])
      }

      "find items" in new ScopedRegistry {
         val shopper = new Shopper(123L,"username")
         val item1   = new ShoppingItem(1L,"item 1")
         val item2   = new ShoppingItem(2L,"item 2")
         registry.shoppingItemRepository.findItems(anyLong) returns List(item1,item2)
         new ShoppingList(12L,"list 1",shopper).findItems must have size 2
      }

      "find no items if none exists" in new ScopedRegistry {
         val shopper = new Shopper(123L,"username")
         registry.shoppingItemRepository.findItems(anyLong) returns Nil
         new ShoppingList(12L,"list 1",shopper).findItems must be empty
      }

      "find item" in new ScopedRegistry {
         val shopper = new Shopper(123L,"username")
         val item1   = new ShoppingItem(1L,"item 1")
         registry.shoppingItemRepository.findItem(anyLong,anyLong) returns Some(item1)
         new ShoppingList(12L,"list 1",shopper).findItem(1L) must beSome
      }

      "not find unknown item" in new ScopedRegistry {
         val shopper = new Shopper(123L,"username")
         registry.shoppingItemRepository.findItem(anyLong,anyLong) returns None
         new ShoppingList(12L,"list 1",shopper).findItem(1L) must beNone
      }

   }

}



