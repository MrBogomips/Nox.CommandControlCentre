package security

import play.api.mvc._

/**
 * This mixin provides authentication and authorization features to basic controllers.
 * 
 * In order to provide Authentication and Authorization features to your controllers you MUST implement
 * a «secured trait» that you'll mix with concrete controllers.
 * 
 * In this example you can see a custom trait that provides the two missing methods to the AuthenticatedController trait:
 * {{{ 
import models.User
trait MySecuredControllerBase extends MyControllerBase with AuthenticatedController[User] {
  def user(request: RequestHeader): Option[User] = request.session.get("login").flatMap(User.findByLogin(_))
  def onUnauthorized(request: RequestHeader) : Result = Results.Redirect(routes.Application.login)
}
 * }}}
 * 
 * In the following lines you can see how to use the security features from your controller:
 * {{{
object MyController extends MySecuredControllerBase {
  def public = Action {Ok}
  def authenticatedWithUserAndRequest = WithAuthentication { (user, request) => Ok}
  def authenticatedWithRequest = WithAuthentication { request => Ok }
  def authenticated = WithAuthentication { Ok }
  def authorizedWithUserAndRequest = WithAuthorization(Allow) { (user, request) => Ok}
  def authorizedWithRequest = WithAuthorization(Allow) { request => Ok }
  def authorized = WithAuthorization(Allow) {Ok}
}
 * }}}
 * 
 * @author Giovanni Costagliola
 */
trait AuthenticatedController[User] {
  /**
   * Retrieve the connected user email.
   * 
   * Mixed class must provide its implementation
   */
  protected def user(request: RequestHeader): Option[User]

  /**
   * Redirect to login if the user in not authorized.
   * 
   * Mixed class must provide its implementation
   */
  protected def onUnauthenticated(request: RequestHeader) : Result 
  
  /**
   * Default handler for unauthorized requests. Default implementation simply returns an HTTP status 443 (FORBIDDEN).
   * Developer can choose a custom handler for a specific request just by providing their own with the WithAuthenticatation(…) block
   * 
   * Mixed class could provide its own implementation by overriding
   */
  protected def onUnauthorized(user: User, request: RequestHeader) : Result = Results.Forbidden 
  
  /**
   * Action wrapper that provide authentication features
   */
  object WithAuthentication {
	  /** 
	   * Action for authenticated users with a custom bodyparser
	   * 
	   * Block can access to the pair (user, request)
	   */
	  def apply[C](bodyParser: BodyParser[C])(f: (User, Request[C]) => Result) : EssentialAction = Security.Authenticated(user, onUnauthenticated) { user =>
	    Action(bodyParser)(request => f(user, request))
	  }
	  
      /** 
	   * Action for authenticated users
	   * 
	   * Block can access to the pair (user, request)
	   */
	  def apply(f: (User, Request[AnyContent]) => Result) : EssentialAction = apply(BodyParsers.parse.anyContent)(f)
	  
	  /** 
	   * Action for authenticated users
	   * 
	   * Block can access to the request
	   */
	  def apply(f: Request[AnyContent] => Result) : EssentialAction = apply(BodyParsers.parse.anyContent)((u:User, r:Request[AnyContent]) => f(r)) 
	  
	  /** 
	   * Action for authenticated users
	   * 
	   * Block doesn't require user nor request
	   */
	  def apply(f: => Result) : EssentialAction =apply(BodyParsers.parse.anyContent)((u:User, r:Request[AnyContent]) => f) 
  }
  
  /**
   * Action wrapper that provide authorization mechanisms
   * 
   * @param rule The autorization rule to validate
   * @param unauthorizedHandler An optional custom handler to manage the unauthorized event
   */
  case class WithAuthorization(rule : AuthorizationRule, unauthorizedHandler : (User, RequestHeader) => Result = onUnauthorized) {
    /** 
	   * Action for authenticated users with a custom bodyparser
	   * 
	   * Block can access to the pair (user, request)
	   */
	  def apply[C](bodyParser: BodyParser[C])(f: (User, Request[C]) => Result) : EssentialAction = WithAuthentication(bodyParser) { (user, request) =>
	    implicit val authorizationContext = AuthorizationContext(user, request)
	    if(rule.eval) {
	      f(user, request)
	    } else {
	      unauthorizedHandler(user, request)
	    } 
	  }
    /** 
   * Action for authorization users.
   * 
   * Block can access to the pair (user, request)
   */
    def apply(f: (User, Request[AnyContent]) => Result): EssentialAction = apply(BodyParsers.parse.anyContent)(f)
   /** 
   * Action for authorization users.
   * 
   * Block can access to the request
   */ 
   def apply(f: Request[AnyContent] => Result): EssentialAction  = apply(BodyParsers.parse.anyContent)((u:User, r:Request[AnyContent]) => f(r)) 
  
    /** 
   * Action for authorization users.
   * 
   * Block doesn't require user nor request
   */
   def apply(f: => Result) : EssentialAction = apply(BodyParsers.parse.anyContent)((u:User, r:Request[AnyContent]) => f)
  }
  
  /**
   * Guarded block with a policy
   * 
   * @param rule The autorization rule to validate
   */
  case class WithPolicy[C](rule : AuthorizationRule)(implicit user: User, request: Request[C]) {
    implicit val authorizationContext = AuthorizationContext(user, request)
    /**
     * Execute the block only if the rule applies
     */
    def apply(f: => Unit): Unit = if (rule.eval) f
    /**
     * Execute the block only if the rule applies
     */
    def apply[A](f: => A): Option[A] = if (rule.eval) Some(f) else None
  }
  /**
   * Guarded block with a policy
   * 
   * @param rule The autorization rule to validate
   */
  case class WithPolicyElse[C](rule : AuthorizationRule)(implicit user: User, request: Request[C]) {
    implicit val authorizationContext = AuthorizationContext(user, request)
    /**
     * Execute the first block if the rule applies, otherwise the «else» block
     */
    def apply(f: => Unit)(els: => Unit): Unit = if (rule.eval) f else els
  }
}