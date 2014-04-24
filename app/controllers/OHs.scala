package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.Action
import play.api.mvc.Controller
import services.SimpleOHListing
import play.api.data.Form
import play.api.data.Forms._
import services.SimpleOH
import org.omg.CosNaming.NamingContextPackage.NotFound
import views.html.defaultpages.badRequest

@Singleton
class OHs @Inject() (ohService: SimpleOHListing) extends Controller {
  def showAll = Action {
    Ok(views.html.oh.ohHome(ohService.list()))
  }

  def show(id: Long) = Action {
    ohService.findById(id).map { oh =>
      Ok(views.html.oh.ohDetail(oh))
    }.getOrElse(NotFound(views.html.errors.on404("A drink with " + id + " id is not found.")))
  }

  def create = Action {
    Ok(views.html.oh.ohNew(ohForm))
  }
  def save = Action { implicit request =>
    ohForm.bindFromRequest.fold(
      errors => BadRequest(views.html.oh.ohNew(errors)),

      ohListing => {
        ohService.create(ohListing)
        Redirect(routes.OHs.showAll)
      })
  }

  def edit(id: Long) = Action {
    ohService.findById(id).map { oh =>
      Ok(views.html.oh.ohEdit(id, ohForm.fill(oh)))
    }.getOrElse(NotFound(views.html.errors.on404("An alcohol was not found.")))
  }

  def update(id: Long) = Action { implicit request =>
    ohForm.bindFromRequest.fold(
      errors => BadRequest(views.html.oh.ohEdit(id, errors)),
      ohListing => {
        val hasUpdated = ohService.update(id, ohListing)
        if (hasUpdated) {
          Redirect(routes.OHs.show(id))
        } else {
          Redirect(routes.OHs.edit(id))
        }
      })
  }

  def delete(id: Long) = Action {
    ohService.delete(id)
    Redirect(routes.OHs.showAll)
  }

  val ohForm: Form[SimpleOH] = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "size" -> text,
      "created_at" -> jodaDate("yyyy-MM-dd"),
      "updated_at" -> jodaDate("yyyy-MM-dd"))(SimpleOH.apply)(SimpleOH.unapply))
}
