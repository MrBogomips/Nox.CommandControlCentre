package playguard

import play.api.mvc._
/**
 * That trait required to be implemented by a Controller integrated within
 * playguard
 */
trait Authenticator[U] {
  def user: RequestHeader => Option[U]
  def onUnauthenticated: RequestHeader => Result 
  def onUnauthorized: RequestHeader => Result 
}
