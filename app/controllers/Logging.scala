package controllers

import play.api._
import play.api.mvc._

import models.LogEntry

object Logging extends Controller {
  
  def index = Action {
    Ok(views.html.logging.index(LogEntry.all()))
  }
  
}