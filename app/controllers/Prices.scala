package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import javax.inject.Inject
import javax.inject.Singleton
import services.SimpleDrinkPriceListing
import play.api.data.Form
import play.api.data.Forms._
import services.SimpleDrinkPrice

@Singleton
class Prices @Inject() (priceService: SimpleDrinkPriceListing) extends Controller {

  def save = Action { implicit request =>
    Prices.priceForm.bindFromRequest.fold(
      errors => { BadRequest("Bad Inputs")
        Redirect(routes.Biz.showAllBiz)
      },

      priceListing => {
        priceService.create(priceListing)
        Redirect(routes.Biz.showBiz(priceListing.bizId.getOrElse(1)))
      })
  }
}

object Prices {
  val priceForm: Form[SimpleDrinkPrice] = Form(
    mapping(
      "id" -> optional(longNumber),
      "biz_id" -> optional(longNumber),
      "oh_id" -> optional(longNumber),
      "price" -> bigDecimal(9, 2))
    (SimpleDrinkPrice.apply)(SimpleDrinkPrice.unapply))
}
