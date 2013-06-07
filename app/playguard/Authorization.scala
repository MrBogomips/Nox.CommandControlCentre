/**
 * Authorization facilities
 *
 * @author Giovanni Costagliola
 */
package playguard

import play.api._
import play.api.mvc.{ Request, RequestHeader }

/**
 * The context whithin a ContextualAuthorizationRule is evaluated
 */
abstract class AuthorizationContext
/**
 * Dummy context
 */
object EmptyAuthorizationContext extends AuthorizationContext
/**
 * An authorization context wich provide User info the the rule
 */
class UserAuthorizationContext[U](implicit val user: U) extends AuthorizationContext
/**
 * An authorization context wich provide User info the the rule
 */
object UserAuthorizationContext {
  def apply[U](implicit user: U) = new UserAuthorizationContext[U]
}
/**
 * A generic HttpAuthorizationContext designed to support Authorization policies at Controller level
 */
abstract class HttpAuthorizationContext[U](implicit user: U) extends UserAuthorizationContext[U]

/**
 * The context passed amongst the authorization rules during the validation process of an HTTP request.
 * The context provide full access to the whole HTTP request i.e. headers and body
 */
case class RequestAuthorizationContext[U, C](implicit override val user: U, request: Request[C]) extends HttpAuthorizationContext[U]

/**
 * The context passed amongst the authorization rules during the validation process of an HTTP request.
 * The context provide access to the HTTP headers only.
 */
case class RequestHeaderAuthorizationContext[U](implicit override val user: U, request: RequestHeader) extends HttpAuthorizationContext[U]

/**
 * Essentially a rule that wraps business policies (e.g. Security Policies, Functional Policies and so on) 
 */
abstract class AuthorizationRule {
  def eval: Boolean
  def &&(that: AuthorizationRule) = And(this, that)
  def ||(that: AuthorizationRule) = Or(this, that)
}
/**
 * An authorization rule that requires a context
 */
abstract class ContextualAuthorizationRule[+A <: AuthorizationContext](implicit cx: A) extends AuthorizationRule
/**
 * An authorization rule that requires a context that provide access to the current user
 */
abstract class UserAuthorizationRule[U](implicit cx: UserAuthorizationContext[U]) extends ContextualAuthorizationRule[UserAuthorizationContext[U]]
/**
 * An authorization rule for Http authentication tasks
 */
abstract class HttpAuthorizationRule[U](implicit cx: HttpAuthorizationContext[U]) extends ContextualAuthorizationRule[HttpAuthorizationContext[U]]
/**
 * An authorization rule for Http authentication tasks that provide the access to the request header
 */
abstract class RequestHeaderAuthorizationRule[U](implicit cx: RequestHeaderAuthorizationContext[U]) extends ContextualAuthorizationRule[RequestHeaderAuthorizationContext[U]]
/**
 * An authorization rule for Http authentication tasks that provide the access to the full request
 */
abstract class RequestAuthorizationRule[U, C](implicit cx: RequestAuthorizationContext[U, C]) extends ContextualAuthorizationRule[RequestAuthorizationContext[U, C]]

/**
 * This rule holds if both the lhs and rhs holds.
 * Evaluation is short circuited
 */
case class And(l: AuthorizationRule, r: AuthorizationRule) extends AuthorizationRule {
  def eval = l.eval && r.eval
}
/**
 * This rule holds if any of the lhs and rhs holds.
 * Evaluation is short circuited
 */
case class Or(l: AuthorizationRule, r: AuthorizationRule) extends AuthorizationRule {
  def eval = l.eval || r.eval
}

/**
 * This rule holds if any of the policies hold
 * Evaluation is short circuited
 */
case class Any(t: AuthorizationRule*) extends AuthorizationRule {
  def eval = t.exists(_.eval)
}
/**
 * This rule holds if all the policies hold
 * Evaluation is short circuited
 */
case class All(t: AuthorizationRule*) extends AuthorizationRule {
  def eval = !t.exists(!_.eval)
}
/**
 * This rule holds if none of the policies hold
 * Evaluation is short circuited
 */
case class NotAny(t: AuthorizationRule*) extends AuthorizationRule {
  def eval = t.exists(!_.eval)
}
/**
 * This rule holds if the wrapped one doesn't
 */
case class Not(t: AuthorizationRule) extends AuthorizationRule {
  def eval = !t.eval
}
/**
 * This rule always holds
 */
object Allow extends AuthorizationRule {
  def eval = true
  override def toString = "Allow"
}
/**
 * This rule never holds
 */
object Deny extends AuthorizationRule {
  def eval = false
  override def toString = "Deny"
}
  