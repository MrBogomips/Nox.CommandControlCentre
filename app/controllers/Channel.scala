package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Channel extends Controller {
  def index = Action {
    val channels = Json.obj(
    		"channels" -> Json.arr("MTC1", "NOXCOM", "NOXT1", "NOXT2")
    		)
    Ok(channels)
  }
}