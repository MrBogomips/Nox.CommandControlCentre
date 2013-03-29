package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import anorm._

object Login extends Controller {
  
  def index = Action {
    DB.withConnection { 
      implicit conn => val dbversion:String = SQL("Select VERSION() as V")().head[String]("V")
      Ok(views.html.login("DB Backend:: MySql $dbversion."))
    }
  }
}