package shop.model

import org.specs2.mock.Mockito
import shop.infrastructure._
import org.specs2.specification._

trait TestRegistry {

   val registry = new TestComponentRegistry

}

trait RuntimeRegistry {

  implicit val registry = new RuntimeComponentRegistry

  (new RepositoryInitialiser).initialiseDatabase

}

class TestComponentRegistry extends ComponentRegistry with Mockito {

   val datasourceConfig       = mock[DatasourceConfig]
   val shopperRepository      = mock[ShopperRepository]
   val identityRepository     = mock[IdentityRepository]
   val shoppingListRepository = mock[ShoppingListRepository]
   val shoppingItemRepository = mock[ShoppingItemRepository]

}

class CleanDatabase(implicit val registry: ComponentRegistry) extends Scope with Repository {

  (new RepositoryInitialiser).cleanDatabase

}



