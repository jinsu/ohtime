package services

import anorm._
import anorm.SqlParser._
import javax.inject.Inject
import play.api.Play.current
import play.api.data.Forms._
import play.api.db.DB

trait SimpleDrinkPriceListing extends LookupService[SimpleDrinkPrice] {
  def listByBiz(bizId: Long): List[SimpleDrinkPrice]
}

case class SimpleDrinkPrice(
  id: Option[Long],
  bizId: Option[Long],
  ohId: Option[Long],
  price: BigDecimal) {
  lazy val name: String = SimpleDrinkPrice.name(ohId.getOrElse(0))
}

object SimpleDrinkPrice {
  val drinkService = new SimpleDrinkPriceDBService(new SimpleOHDBService)
  def name(ohId: Long): String = {
    drinkService.getOHService.findById(ohId).map { oh =>
      oh.name
    }.getOrElse("Unkown")
  }
}

class SimpleDrinkPriceDBService @Inject() (ohService: SimpleOHListing) extends SimpleDrinkPriceListing {
  def getOHService: SimpleOHListing = ohService

  def list(limit: Int = 10, offset: Int = 0): List[SimpleDrinkPrice] = DB.withConnection { implicit c =>
    SQL("select * from Drink_Price limit {limit}")
      .on('limit -> limit)
      .as(priceParser *)
  }

  def listByBiz(bizId: Long): List[SimpleDrinkPrice] = DB.withConnection { implicit c =>
    SQL("""
        select * from Drink_Price where biz_id = {biz_id}
        """)
      .on('biz_id -> bizId)
      .as(priceParser *)
  }

  def findByAttr(model: SimpleDrinkPrice): Option[SimpleDrinkPrice] = {
    val price: Option[SimpleDrinkPrice] = DB.withConnection { implicit c =>
      SQL("""
           select * from Drink_Price where biz_id = {biz_id}
           and oh_id = {oh_id}
           """)
        .on('biz_id -> model.bizId.get,
          'oh_id -> model.ohId.get)
        .as(priceParser.singleOpt)
    }
    price
  }

  def create(drink: SimpleDrinkPrice): Option[Long] = {
    println("price => " + drink.price + " :: biz_id => " + drink.bizId.get + " :: oh_id => " + drink.ohId.get);
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("""
          insert into Drink_Price
          (price, biz_id, oh_id) values (({price}), ({biz_id}), ({oh_id}))
          """)
        .on('price -> drink.price.bigDecimal,
          'biz_id -> drink.bizId.get,
          'oh_id -> drink.ohId.get)
        .executeInsert()
    }
    id
  }

  def delete(model: SimpleDrinkPrice): Int = {
    val numRowsDeleted = DB.withTransaction { implicit c =>
      SQL("""
          delete from Drink_Price where oh_id = {oh_id}
          and biz_id = {biz_id}
          """)
        .on('oh_id -> model.ohId,
          'biz_id -> model.bizId)
        .executeUpdate()
    }
    numRowsDeleted
  }

  def update(curModel: SimpleDrinkPrice, newModel: SimpleDrinkPrice): Boolean = {
    DB.withConnection({ implicit c =>
      SQL("""
          update Drink_Price set price={price}
          where biz_id={biz_id} and oh_id={oh_id}
          """)
        .on('price -> newModel.price,
          'oh_id -> curModel.ohId,
          'biz_id -> curModel.bizId)
        .executeUpdate() == 1
    })
  }

  val priceParser = {
    get[Long]("id") ~
      get[Long]("biz_id") ~
      get[Long]("oh_id") ~
      get[java.math.BigDecimal]("price") map {
        case id ~ biz_id ~ oh_id ~ price => SimpleDrinkPrice(Option(id), Option(biz_id), Option(oh_id), BigDecimal(price))
      }
  }
}
