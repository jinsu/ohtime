package controllers

import models.Business
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {

  def index = Action {
    Redirect(routes.Application.showBiz)
  }

  def showBiz = Action {
    Ok(views.html.index(Business.all(), bizForm))
  }

  def newBiz = Action { implicit request =>
    bizForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index(Business.all(), errors)),
        name =>  {
          Business.create(name)
          Redirect(routes.Application.showBiz)
        }
    )
  }

  def deleteBiz(id: Long) = Action {
    Business.delete(id)
    Redirect(routes.Application.showBiz)
  }

  val bizForm = Form(
      "name" -> nonEmptyText
  )

}
