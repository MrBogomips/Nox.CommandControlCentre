package security

import play.api.mvc._

trait Authenticator[U] {
  def user: RequestHeader => Option[U]
  def onUnauthenticated: RequestHeader => Result 
  def onUnauthorized: RequestHeader => Result 
}

abstract case class WithAuthentication[U](block: Request[AnyContent] => Result) extends Authenticator[U] {
  def apply = Security.Authenticated(user, onUnauthorized) { _ =>
    Action(request => block(request))
  }
}