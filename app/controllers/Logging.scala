package controllers

import play.api._
import play.api.mvc._

object Logging extends Controller {
  
  def index = Action {
    Ok(views.html.logging.index(""))
  }
  
}