package services

import anorm._
import anorm.SqlParser._;
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.Format
import play.api.libs.json.Json

trait SimpleBusinessListing extends ListingService[SimpleBusiness] {
}

trait DrinkLister {
  val bartender: SimpleDrinkPriceListing

  def listDrinks(bizId: Long): List[SimpleDrinkPrice] = {
    bartender.listByBiz(bizId)
  }
}

case class SimpleBusiness(
  id: Option[Long],
  name: String,
  addr: String) {
  lazy val drinks: List[SimpleDrinkPrice] = {
    SimpleBusiness.drinkService.listDrinks(id.getOrElse(0))
  }
}

object SimpleBusiness {
  implicit val sbmFmt: Format[SimpleBusiness] = Json.format[SimpleBusiness]
  val drinkService = new DrinkLister {
    val bartender = new SimpleDrinkPriceDBService(new SimpleOHDBService)
  }
}

class SimpleBizDBService extends SimpleBusinessListing {

  def list(limit: Int = 10, offset: Long = 0): List[SimpleBusiness] = DB.withConnection { implicit c =>
    SQL("select * from biz limit {limit}")
      .on('limit -> limit)
      .as(bizParser *)
  }

  def findById(id: Long): Option[SimpleBusiness] = {
    val biz: Option[SimpleBusiness] = DB.withConnection { implicit c =>
      SQL("select * from biz where id = {id}")
        .on('id -> id)
        .as(bizParser.singleOpt)
    }
    biz
  }

  def create(bizListing: SimpleBusiness): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into biz (name) values ({name})")
        .on('name -> bizListing.name)
        .executeInsert()
    }
    id
  }

  def delete(id: Long): Int = {
    val numRowsDeleted = DB.withTransaction { implicit c =>
      SQL("delete from biz where id = {id}")
        .on('id -> id)
        .executeUpdate()
    }
    numRowsDeleted
  }

  def update(id: Long, biz: SimpleBusiness): Boolean = {
    DB.withConnection({ implicit c =>
      SQL("update biz set name={name} where id={id}")
        .on('name -> biz.name,
          'id -> id)
        .executeUpdate() == 1
    })
  }

  val bizParser = {
    get[Long]("id") ~
      get[String]("name") ~
      get[String]("address") map {
        case id ~ name ~ address=> SimpleBusiness(Option(id), name, address)
      }
  }
}
