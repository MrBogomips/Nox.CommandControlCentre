package controllers

import play.api._
import play.api.mvc._

import controllers._
import models._

object Map2 extends Secured {
  
  def index = IsAuthenticated { login => implicit request =>
    Ok(views.html.aria.map.index())
  }
  
}