package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ User => UserModel, UserPersisted, Users }

import org.joda.time.format.ISODateTimeFormat

import models.json.userPersistedJsonWriter

object User extends Secured {
  def getCurrent = WithCors("GET") {
    WithAuthentication { (user, request) =>
      Ok(Json.toJson(user))
    }
  }
}