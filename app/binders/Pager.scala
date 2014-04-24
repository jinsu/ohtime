package binders

import play.api.mvc.QueryStringBindable

case class Pager(limit: Int, offset: Long)

object Pager {
  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int], longBinder: QueryStringBindable[Long]) = new QueryStringBindable[Pager] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Pager]] = {
      for {
        limit <- intBinder.bind(key + ".limit", params)
        offset <- longBinder.bind(key + ".offset", params)
      } yield {
        (limit, offset) match {
          case (Right (limit), Right(offset)) => Right(Pager(limit, offset))
          case _ => Left("Unable to bind a Pager")
        }
      }
    }

    override def unbind(key: String, pager: Pager): String = {
      intBinder.unbind(key + ".limit", pager.limit) + "&" + longBinder.unbind(key + ".offset", pager.offset)
    }
  }
}
