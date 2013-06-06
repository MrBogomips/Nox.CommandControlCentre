/**
 * Authorization facilities
 * 
 * @author Giovanni Costagliola
 */
package security

import play.api._
import models.User
import play.api.mvc.{Request, AnyContent}

/**
 * The context passed amongst the authorization rules during the validation process
 */
case class AuthorizationContext[U,C](user: U, request: Request[C])

/**
 * The generic rule
 */
abstract class AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean
}
/**
 * This rule holds if both the lhs and rhs holds.
 * Evaluation is short circuited
 */
case class And(l: AuthorizationRule, r: AuthorizationRule) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]) = l.eval && r.eval
}
/**
 * This rule holds if any of the lhs and rhs holds.
 * Evaluation is short circuited
 */
case class Or(l: AuthorizationRule, r: AuthorizationRule) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]) = l.eval || r.eval
}
/**
 * This rule holds if any of the policies hold
 * Evaluation is short circuited
 */
case class Any(t: AuthorizationRule*) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = t.exists(_.eval)
}
/**
 * This rule holds if all the policies hold
 * Evaluation is short circuited
 */
case class All(t: AuthorizationRule*) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = !t.exists(!_.eval)
}
/**
 * This rule holds if none of the policies hold
 * Evaluation is short circuited
 */
case class NotAny(t: AuthorizationRule*) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = t.exists(!_.eval)
}
/**
 * This rule holds if the wrapped one doesn't
 */
case class Not(t: AuthorizationRule) extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]): Boolean = !t.eval
}
/**
 * This rule always holds
 */
object Allow extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]) = true
}
/**
 * This rule never holds
 */
object Deny extends AuthorizationRule {
  def eval[U,C](implicit cx : AuthorizationContext[U,C]) = false
}
  