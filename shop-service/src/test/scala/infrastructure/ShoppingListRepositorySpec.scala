package shop.repository

import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.mock.Mockito
import shop.service._
import shop.model._
import shop.infrastructure._
import org.h2.jdbc.JdbcSQLException


class ShoppingListRepositorySpec extends Specification with RuntimeRegistry with Repository {

   val shoppingListRepository = registry.shoppingListRepository
   val shopperRepository      = registry.shopperRepository

   "ShoppingList repository" should {

      "not find list" in {
         val list = shoppingListRepository.findList(123L)
         list must beNone
      }

      "save list" in {
         val listId = for{
            shopper <- new Shopper("username6").save
            listId  <- shoppingListRepository.save(new ShoppingList("list",shopper))
         } yield listId
         listId must beSome(beGreaterThan(0L))
      }

      "find list" in {
         val list = for{
            shopper <- new Shopper("username7").save
            listId  <- shoppingListRepository.save(new ShoppingList("list",shopper))
            list    <- shoppingListRepository.findList(listId)
         } yield list
         list must beSome
      }

      "find owner lists" in {
         val lists = for{
            shopper   <- new Shopper("username8").save.toList
            shopperId <- shopper.id.toList
            l1        <- shoppingListRepository.save(new ShoppingList("list1",shopper)).toList
            l2        <- shoppingListRepository.save(new ShoppingList("list2",shopper)).toList
            list      <- shoppingListRepository.findOwnerLists(shopperId)
         } yield list
         lists must have size 2
      }

      "not find others lists" in {
         val lists = for{
            shopper1   <- new Shopper("username111").save.toList
            shopper2   <- new Shopper("username222").save.toList
            shopperId1 <- shopper1.id.toList
            shopperId2 <- shopper2.id.toList
            listId1    <- shoppingListRepository.save(new ShoppingList("list1",shopper1)).toList
            listId2    <- shoppingListRepository.save(new ShoppingList("list2",shopper1)).toList
            list       <- shoppingListRepository.findOwnerLists(shopperId2)
         } yield list
         lists must be empty
      }

      "add participant to list" in {
         val lists = for{
            shopper1   <- new Shopper("userna213sdfdsf2me111").save.toList
            shopper2   <- new Shopper("usedsfdsfrna1232me222").save.toList
            shopperId1 <- shopper1.id.toList
            shopperId2 <- shopper2.id.toList
            listId1    <- shoppingListRepository.save(new ShoppingList("list1",shopper1)).toList
            part1      <- shoppingListRepository.addParticipantToList(shopperId2,listId1).toList
            list       <- shoppingListRepository.findParticipantLists(shopperId2)
         } yield list
         lists must have size 1
      }

      "find participant list" in {
         val lists = for{
            shopper1   <- new Shopper("userna2132me111").save.toList
            shopper2   <- new Shopper("userna1232me222").save.toList
            shopperId1 <- shopper1.id.toList
            shopperId2 <- shopper2.id.toList
            listId1    <- shoppingListRepository.save(new ShoppingList("list1",shopper1)).toList
            listId2    <- shoppingListRepository.save(new ShoppingList("list2",shopper1)).toList
            part1      <- shoppingListRepository.addParticipantToList(shopperId2,listId1).toList
            part2      <- shoppingListRepository.addParticipantToList(shopperId2,listId2).toList
            list       <- shoppingListRepository.findParticipantLists(shopperId2)
         } yield list
         lists must have size 2
      }
   }
}
