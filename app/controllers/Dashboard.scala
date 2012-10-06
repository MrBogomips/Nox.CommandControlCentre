package controllers

import play.api._
import play.api.mvc._

object Dashboard extends Controller {
  
  def index = Action {
    Ok(views.html.dashboard("Your new application is ready."))
  }
  
}