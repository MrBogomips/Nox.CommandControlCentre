package controllers

import play.api._
import play.api.mvc._

object Map2 extends Controller {
  val conf = play.Configuration.root()
  def index = Action {
    
    Ok(views.html.map2.index())
    //Ok(views.html.map.index("http://localhost:5000"))
  }
  
}