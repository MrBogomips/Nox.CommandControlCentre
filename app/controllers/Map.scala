package controllers

import play.api._
import play.api.mvc._

object Map extends Controller {
  
  def index = Action {
    Ok(views.html.map.index())
    //Ok(views.html.map.index("http://localhost:5000"))
  }
  
}