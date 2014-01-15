package controllers.tests

import play.api._
import play.api.mvc
import play.api.mvc._
import play.api.libs._
import play.api.libs.iteratee._

import controllers.Secured
import views.utils.context._

object DataTable extends Secured with ProvidesViewContext {
  def index = WithAuthentication { (user, request) =>
    Ok(views.html.datatable.index("ciccio", user))
  }
  
  def index2 = WithAuthentication { (user, request) =>
    Ok(views.html.datatable.index2("ciccio", user))
  }
}