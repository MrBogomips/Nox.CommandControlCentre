package controllers

import play.api._
import play.api.mvc._

object Map2 extends Controller {
  val conf = play.Configuration.root()
  def index = Action {
    Ok(views.html.aria.map.index())
  }
  
}