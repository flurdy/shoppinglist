package shop.repository

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.mock.Mockito
import shop.service._
import shop.model._
import shop.infrastructure._
import org.h2.jdbc.JdbcSQLException


class ShoppingItemRepositorySpec extends Specification with RuntimeRegistry with Repository {

   val shoppingItemRepository = registry.shoppingItemRepository
   val shoppingListRepository = registry.shoppingListRepository
   val shopperRepository      = registry.shopperRepository

   "ShoppingItem repository" should {

      "not find item" in {
         val item = shoppingItemRepository.findItem(123L,123L)
         item must beNone
      }

      "not find list of items" in {
         val items = shoppingItemRepository.findItems(123L)
         items must be empty
      }

      "save item" in {
         val itemId = for {
            shopper   <- new Shopper("usernssadsaame8").save
            shopperId <- shopper.id
            list      <- new ShoppingList("list1",shopper).save
            item      <- new ShoppingItem("item").save(list)
            itemId    <- item.id
         } yield itemId
         itemId must beSome(beGreaterThan(0L))
      }

      "find item" in {
         val newName = for {
            shopper <- new Shopper("usernadasdsasaame8").save
            list    <- new ShoppingList("list1",shopper).save
            listId  <- list.id
            item1   <- new ShoppingItem("item").save(list)
            itemId  <- item1.id
            item2   <- shoppingItemRepository.findItem(listId,itemId)
         } yield item2.name
         newName must beSome(beEqualTo("item"))
      }

      "update item" in {
         val newName = for {
            shopper      <- new Shopper("userndsfdsfadasdsasaame8").save
            list         <- new ShoppingList("list1",shopper).save
            listId       <- list.id
            itemItem     <- new ShoppingItem("item").save(list)
            itemId       <- itemItem.id
            itemFound    <- shoppingItemRepository.findItem(listId,itemId)
            another      <- itemItem.copy(name="another").update
            anotherFound <- shoppingItemRepository.findItem(listId,itemId)
         } yield anotherFound.name
         newName must beSome(beEqualTo("another"))
      }

      "find list of items" in {
         val items = for {
            shopper <- new Shopper("usernadsadfdsfasdsasaame8").save.toList
            list    <- new ShoppingList("list1",shopper).save.toList
            listId  <- list.id.toList
            _       <- new ShoppingItem("item1").save(list).toList
            _       <- new ShoppingItem("item2").save(list).toList
            item    <- shoppingItemRepository.findItems(listId)
            } yield item
         items must have size 2
      }

   }
}
