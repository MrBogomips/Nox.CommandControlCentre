package controllers.tests

import play.api._
import play.api.mvc
import play.api.mvc._
import play.api.libs._
import play.api.libs.iteratee._

import controllers.Secured
import views.utils.context._

object Events extends Secured with ProvidesViewContext {

  def index = WithAuthentication { (user, request) =>
    play.api.Play.current.mode match {
      //case Mode.Dev | Mode.Test =>
      case _ =>
        implicit val r = request
        implicit val u = user
        val wsUrl = controllers.routes.Events.channel.webSocketURL()
        Ok(views.html.tests.events.index(user, wsUrl))
      //case _ =>
        //NotFound("")
    }

  }
}