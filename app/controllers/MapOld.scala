package controllers

import play.api._
import play.api.mvc._

object MapOld extends Controller {
  
  def index = Action {
    Ok(views.html.map.index())
  }
  
}