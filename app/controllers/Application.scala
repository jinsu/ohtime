package controllers

import play.api.mvc.Action
import play.api.mvc.Controller

import javax.inject.Singleton

@Singleton
object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

}
