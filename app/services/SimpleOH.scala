package services

import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._;
import extensions.AnormExtension._
import org.joda.time.DateTime

trait SimpleOHListing extends ListingService[SimpleOH] {}

case class SimpleOH(
    id: Option[Long],
    name: String,
    size: String,
    created_at: DateTime,
    updated_at: DateTime
)

object SimpleOH {
  implicit val sohFmt: Format[SimpleOH] = Json.format[SimpleOH]
}

class SimpleOHDBService extends SimpleOHListing {
  def list(limit:Int = 10, offset:Long = 0): List[SimpleOH] = DB.withConnection { implicit c =>
    SQL("select * from OH limit {limit}")
    .on('limit -> limit)
    .as(ohParser *)
  }

  def findById(id: Long): Option[SimpleOH] = {
     val entry: Option[SimpleOH] = DB.withConnection { implicit c =>
       SQL("select * from OH where id = {id}")
       .on('id -> id)
       .as(ohParser.singleOpt)
    }
    entry
  }

  def create(OH: SimpleOH): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit c =>
      SQL("insert into OH (name) values ({name})")
      .on('name -> OH.name)
      .executeInsert()
    }
    id
  }

  def delete(id: Long): Int = {
    val numRowsDeleted = DB.withTransaction { implicit c =>
      SQL("delete from OH where id = {id}")
      .on('id -> id)
      .executeUpdate()
    }
    numRowsDeleted
  }

  def update(id: Long, OH: SimpleOH): Boolean = {
    DB.withConnection({ implicit c =>
      SQL("update OH set name={name} where id={id}")
      .on('name -> OH.name,
          'id -> id)
      .executeUpdate() == 1})
  }

  val ohParser = {
    get[Long]("id") ~
    get[String]("name") ~
    get[String]("size") ~
    get[DateTime]("created_at") ~
    get[DateTime]("updated_at") map {
      case id ~ name ~ size ~ created_at ~ updated_at => SimpleOH(Option(id), name, size, created_at, updated_at)
    }
  }
}
