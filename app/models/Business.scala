package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Business(id: Long, name: String)

object Business {

  def all(): List[Business] = DB.withConnection { implicit c =>
    SQL("select * from biz").as(biz *)
  }

  def create(name: String) {
    DB.withConnection { implicit c =>
      SQL("insert into biz (name) values ({name})").on(
          'name -> name
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withTransaction { implicit c =>
      SQL("delete from biz where id = {id}").on(
          'id -> id
      ).executeUpdate()
    }
  }

  val biz = {
    get[Long]("id") ~
    get[String]("name") map {
      case id~name => Business(id, name)
    }
  }
}
