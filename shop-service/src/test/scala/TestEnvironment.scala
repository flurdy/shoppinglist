package shop.model

import org.specs2.mock.Mockito
import org.mockito.Mockito._
import shop.infrastructure._
import org.specs2.specification._

trait TestRegistry {

   implicit val registry = new TestComponentRegistry

}

trait RuntimeRegistry {

  implicit val registry = new RuntimeComponentRegistry

  (new RepositoryInitialiser).initialiseDatabase

}

class TestComponentRegistry extends ComponentRegistry {

   val datasourceConfig       = mock(classOf[DatasourceConfig])
   val shopperRepository      = mock(classOf[ShopperRepository])
   val identityRepository     = mock(classOf[IdentityRepository])
   val shoppingListRepository = mock(classOf[ShoppingListRepository])
   val shoppingItemRepository = mock(classOf[ShoppingItemRepository])

}

trait ScopedRegistry extends Scope with TestRegistry{

}

class CleanDatabase(implicit val registry: ComponentRegistry) extends Scope with Repository {

  (new RepositoryInitialiser).cleanDatabase

}



