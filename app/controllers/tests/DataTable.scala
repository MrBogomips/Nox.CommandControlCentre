package controllers.tests

import play.api._
import play.api.mvc
import play.api.mvc._
import play.api.libs._
import play.api.libs.iteratee._

import controllers.Secured
import views.utils.context._

import play.api.libs.json._

object DataTable extends Secured with ProvidesViewContext {
  
  def index = WithAuthentication { (user, request) =>
    Ok(views.html.datatable.index("ciccio", user))
  }
  
  def serverSidePoc = WithAuthentication { (user, request) =>
    Ok(views.html.datatable.integrationPoc("ciccio", user))
  }
  
  def fakeData = Action {
    val json = Json.parse("""
			      {
			  "sEcho": 0,
			  "iTotalRecords": "57",
			  "iTotalDisplayRecords": "57",
			  "aaData": [
			    [
			      "Gecko",
			      "Firefox 1.0",
			      "Win 98+ / OSX.2+",
			      "1.7",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Firefox 1.5",
			      "Win 98+ / OSX.2+",
			      "1.8",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Firefox 2.0",
			      "Win 98+ / OSX.2+",
			      "1.8",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Firefox 3.0",
			      "Win 2k+ / OSX.3+",
			      "1.9",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Camino 1.0",
			      "OSX.2+",
			      "1.8",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Camino 1.5",
			      "OSX.3+",
			      "1.8",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Netscape 7.2",
			      "Win 95+ / Mac OS 8.6-9.2",
			      "1.7",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Netscape Browser 8",
			      "Win 98SE+",
			      "1.7",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Netscape Navigator 9",
			      "Win 98+ / OSX.2+",
			      "1.8",
			      "A"
			    ],
			    [
			      "Gecko",
			      "Mozilla 1.0",
			      "Win 95+ / OSX.1+",
			      "1",
			      "A"
			    ]
			  ]
			}
      """)
      
      Ok(json)
  }
  
  def index2 = WithAuthentication { (user, request) =>
    Ok(views.html.datatable.index2("ciccio", user))
  }
}