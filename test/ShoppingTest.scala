import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.Logger

import models._

class ShopperSpec extends Specification {

  "A Shopper" should {
    "be able to register" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Shopper.create(new Shopper("thirduser","")) must beAnInstanceOf[Shopper]
      }
    }
    "must have a unique username" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Shopper.create(new Shopper("thirduser","")) must beAnInstanceOf[Shopper]
        Shopper.create(new Shopper("thirduser",""))  must throwAn[Exception]
      }
    }
//    "must have a username" in {
//      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
//        Shopper.create(new Shopper("",""))  must throwAn[Exception]
//      }
//    }
  }
}



class ShoppingListSpec extends Specification {
  "The Shopping list " should {
    "display 4 items for testuser" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        ShoppingList.findItemsByUsername("testuser") must have size(4)
      }
    }
    "display 0 items for otheruser" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        ShoppingList.findItemsByUsername("otheruser") must be empty
      }
    }
    "throw error for unknownuser" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Shopper.findByUsername("unknownuser").get.findItems must throwAn[Exception]
      }
    }
    "bananas is on testusers list" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        ShoppingList.findItemByName("testuser","Bananas").get.name must beEqualTo("Bananas")
      }
    }
    "Apples is not on testusers list" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        ShoppingList.findItemByName("testuser","Apples") must beNone
      }
    }
    "be able to remove an item" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val shoppingList = ShoppingList.findListByUsername("testuser").get
        val item = shoppingList.addItem(new ShoppingItem("Burgers"))
        shoppingList.findItemByName("Burgers") must beSome
        shoppingList.removeItem("Burgers")
        shoppingList.findItemByName("Burgers") must beNone
      }
    }
  }
}




class ShoppingItemSpec extends Specification {
  "A shopping item" should {
    "be able to have the same name for two different people's list" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val testusersList = ShoppingList.findListByUsername("testuser").get
        testusersList.id.get must beGreaterThan(0L)
        val testitem:ShoppingItem = testusersList.addItem(new ShoppingItem("Burgers"))
        testitem must beAnInstanceOf[ShoppingItem]

        val otherusersList = ShoppingList.findListByUsername("otheruser").get
        otherusersList.id.get must beGreaterThan(1L)
        val otheritem:ShoppingItem = otherusersList.addItem(new ShoppingItem("Burgers"))
        otheritem must beAnInstanceOf[ShoppingItem]
        otheritem.id must not be equalTo(testitem.id)
      }
    }

    "mark as purchased" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val testusersList = ShoppingList.findListByUsername("testuser").get
        val apples = testusersList.addItem(new ShoppingItem("Apples"))
        testusersList.findItemByName("Apples").get.isPurchased must beFalse
        apples.storeAsPurchased
        testusersList.findItemByName("Apples").get.isPurchased must beTrue
      }
    }
    "if added by the same name, will mark as not purchased if purchased" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val testusersList = ShoppingList.findListByUsername("testuser").get
        val apples = testusersList.addItem(new ShoppingItem("Apples"))
        apples.id.get must beGreaterThan(4L)
        testusersList.findItemByName("Apples").get.isPurchased must beFalse
        apples.storeAsPurchased
        testusersList.findItemByName("Apples").get.isPurchased must beTrue
        val apples2 = testusersList.addItem(new ShoppingItem("Apples"))
        testusersList.findItemByName("Apples").get.isPurchased must beFalse
      }
    }
    "if added by the same name, will still be not purchased if not purchased" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val testusersList = ShoppingList.findListByUsername("testuser").get
        val apples = testusersList.addItem(new ShoppingItem("Apples"))
        apples.id.get must beGreaterThan(4L)
        testusersList.findItemByName("Apples").get.isPurchased must beFalse
        val apples2 = testusersList.addItem(new ShoppingItem("Apples"))
        testusersList.findItemByName("Apples").get.isPurchased must beFalse
      }
    }
    "be able update name" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val shoppingList = ShoppingList.findListByUsername("testuser").get
        val item = shoppingList.addItem(new ShoppingItem("Burgers"))
        shoppingList.findItemByName("Burgers") must beSome
        val burgers = shoppingList.findItemByName("Burgers").get
        ShoppingItem.update(new ShoppingItem(burgers.id,"Hamburgers",burgers.description,burgers.isPurchased,burgers.listId))
        shoppingList.findItemByName("Burgers") must beNone
        shoppingList.findItemByName("Hamburgers") must beSome
      }
    }
  }
} 