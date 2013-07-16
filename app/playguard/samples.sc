package playguard

import play.api._
import play.api.mvc._
import security._
import controllers._

object samples {
  object MyController extends Secured {
    def public = Action { Ok("") }
    def authenticatedWithUserAndRequest = WithAuthentication { (user, request) => Ok }
    def authenticatedWithRequest = WithAuthentication { request => Ok }
    def authenticated = WithAuthentication { Ok }
    def authorizedWithUserAndRequest = WithAuthorization(Allow) { (user, request) => Ok }
    def authorizedWithRequest = WithAuthorization(Allow) { request => Ok }
    def authorized = WithAuthorization(Allow) { Ok }
    def receiveJsonAuthenticated = WithAuthentication(parse.json) { (user, json) =>
      Ok
    }
    def receiveJsonAuthorized = WithAuthorization(Allow)(parse.json) { (user, json) =>
      Ok
    }

  }
}