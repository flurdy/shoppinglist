package shop.model

import shop.infrastructure._


case class Shopper(id: Option[Long], username: String)

object Shoppers {
   
   def findShopper = ShopperRepository.findShopper _

}
