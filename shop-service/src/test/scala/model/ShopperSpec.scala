package shop.model

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.mock.Mockito
import shop.service._
import shop.model._
import shop.infrastructure._

class ShopperSpec extends Specification with Mockito {

   "Shopper model" should {

      "not save existing username" in new ScopedRegistry {
         registry.shopperRepository.findShopper(anyString) returns Some(Shopper(Some(123L),"username"))
         new Shopper("username").save must beNone
         there was no(registry.shopperRepository).save(anyString)
      }

      "save" in new ScopedRegistry {
         registry.shopperRepository.findShopper(anyString) returns None
         registry.shopperRepository.save(anyString) returns Some(123L)
         new Shopper("username").save must beSome
         there was one(registry.shopperRepository).save(anyString)
      }

      "find lists" in new ScopedRegistry {
         val shopper = new Shopper(123L,"username")
         val list1   = new ShoppingList(12L,"list 1",shopper)
         val list2   = new ShoppingList(13L,"list 2",shopper)
         registry.shoppingListRepository.findOwnerLists(anyLong) returns List(list1,list2)
         shopper.findLists must have size 2
      }

      "find no lists if none exists" in new ScopedRegistry {
         registry.shoppingListRepository.findOwnerLists(anyLong) returns Nil
         new Shopper(123L,"username").findLists must be empty
      }

      "find no lists if no id yet" in new ScopedRegistry {
         new Shopper("username").findLists must beEmpty
         there was no(registry.shoppingListRepository).findOwnerLists(anyLong)
      }

      "find other lists" in new ScopedRegistry {
         val shopper1 = new Shopper(123L,"username1")
         val shopper2 = new Shopper(345L,"username2")
         val list1    = new ShoppingList(12L,"list 1",shopper1)
         val list2    = new ShoppingList(13L,"list 2",shopper1)
         registry.shoppingListRepository.findParticipantLists(anyLong) returns List(list1,list2)
         shopper2.findOtherLists must have size 2
      }


   }

}



