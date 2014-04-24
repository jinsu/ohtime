package controllers

import binders.Pager
import javax.inject.Inject
import javax.inject.Singleton
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import services.SimpleBusiness
import services.SimpleBusinessListing
import services.SimpleOHListing

@Singleton
class Biz @Inject() (bizService: SimpleBusinessListing, ohService: SimpleOHListing) extends Controller {

  def showAllBiz = Action {
    Ok(views.html.biz.bizHome(bizService.list()))
  }

  def showAllBizAsJson(p: Pager) = Action { implicit request =>
    Ok(Json.toJson(bizService.list(p.limit, p.offset).map { biz =>
      BizDetail(biz.id.getOrElse(0), biz.name, biz.drinks.map { drink => (drink.name, drink.price)}.toMap)
    }))
  }

  def showBiz(id: Long) = Action {
    var oh_ids = ohService.list().map { oh => oh.id.get.toString }
    bizService.findById(id).map { biz =>
      Ok(views.html.biz.bizDetail(biz, oh_ids, Prices.priceForm))
    }.getOrElse(NotFound(views.html.errors.on404("A business with " + id + " as its id.")))
  }

  def create = Action {
    Ok(views.html.biz.bizNew(bizForm))
  }

  def save = Action { implicit request =>
    bizForm.bindFromRequest.fold(
      // on error, go back to the form.
      errors => BadRequest(views.html.biz.bizNew(errors)),
      // on success, create the business. go to index page.
      bizListing => {
        bizService.create(bizListing)
        Redirect(routes.Biz.showAllBiz)
      })
  }

  def edit(id: Long) = Action {
    bizService.findById(id).map { biz =>
      Ok(views.html.biz.bizEdit(id, bizForm.fill(biz)))
    }.getOrElse(NotFound(views.html.errors.on404("A business with " + id + " as its id.")))
  }

  def update(id: Long) = Action { implicit request =>
    bizForm.bindFromRequest.fold(
        errors => BadRequest(views.html.biz.bizEdit(id, errors)),
        bizListing => {
          val hasUpdated = bizService.update(id, bizListing)
          if (hasUpdated) {
            Redirect(routes.Biz.showBiz(id))
          } else {
            Redirect(routes.Biz.edit(id))
          }
        })
  }

  def delete(id: Long) = Action {
    bizService.delete(id)
    Redirect(routes.Biz.showAllBiz)
  }

  case class BizDetail(id: Long, name: String, drinks: Map[String, BigDecimal])
  object BizDetail {
    implicit val bdFmt: Format[BizDetail] = Json.format[BizDetail]
  }

  val bizForm: Form[SimpleBusiness] = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText)(SimpleBusiness.apply)(SimpleBusiness.unapply))
}
