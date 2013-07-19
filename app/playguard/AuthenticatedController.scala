package playguard

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._

import patterns.models.ValidationException
import models.json.seqValidationErrorJsonWriter

/**
  * A Controller that can apply a @see WithRule constraint
  *
  *
  * {{{
  * object MyController extends PolicyBasedController {
  * 	def receiveWithASimpleRule = WithRule(Allow)(parse.json) { json => Ok }
  * }
  * }}}
  */
trait PolicyBasedController {
  /**
    * Default handler for unauthorized requests. Default implementation simply returns an HTTP status 443 (FORBIDDEN).
    * Developer can choose a custom handler for a specific request just by providing their own with the WithAuthenticatation(…) block
    *
    * Mixed class could provide its own implementation by overriding
    */
  protected def onUnauthorized(request: RequestHeader): Result = Results.Forbidden

  /**
    * Action wrapper that provide authentication features
    */
  case class WithRule(rule: AuthorizationRule, unauthorizedHandler: RequestHeader => Result = onUnauthorized) {
    /**
      * Action for authenticated users with a custom bodyparser
      *
      * Block can access to the pair (user, request)
      */
    def apply[C](bodyParser: BodyParser[C])(f: (Request[C]) => Result): EssentialAction = Action(bodyParser) { request =>
      if (rule.eval) {
        f(request)
      } else {
        unauthorizedHandler(request)
      }
    }

    /**
      * Action
      *
      * Block can access to the pair (user, request)
      */
    def apply(f: Request[AnyContent] => Result): EssentialAction = apply(BodyParsers.parse.anyContent)(f)

    /**
      * Action
      *
      * Block doesn't require user nor request
      */
    def apply(f: => Result): EssentialAction = apply(BodyParsers.parse.anyContent)((r: Request[AnyContent]) => f)
  }
}

/**
  * This mixin provides authentication and authorization features to basic controllers.
  *
  * In order to provide Authentication and Authorization features to your controllers you MUST implement
  * a «secured trait» that you'll mix with concrete controllers.
  *
  * In this example you can see a custom trait that provides the two missing methods to the AuthenticatedController trait:
  * {{{
  * import models.User
  * trait MySecuredControllerBase extends MyControllerBase with AuthenticatedController[User] {
  * def user(request: RequestHeader): Option[User] = request.session.get("login").flatMap(User.findByLogin(_))
  * def onUnauthorized(request: RequestHeader) : Result = Results.Redirect(routes.Application.login)
  * }
  * }}}
  *
  * In the following lines you can see how to use the security features from your controller:
  * {{{
  * object MyController extends MySecuredControllerBase {
  * def public = Action {Ok}
  * def authenticatedWithUserAndRequest = WithAuthentication { (user, request) => Ok}
  * def authenticatedWithRequest = WithAuthentication { request => Ok }
  * def authenticated = WithAuthentication { Ok }
  * def authorizedWithUserAndRequest = WithAuthorization(Allow) { (user, request) => Ok}
  * def authorizedWithRequest = WithAuthorization(Allow) { request => Ok }
  * def authorized = WithAuthorization(Allow) {Ok}
  * def receiveJsonAuthenticated = WithAuthentication(parse.json) { (user, json) =>
  * Ok
  * }
  * def receiveJsonAuthorized = WithAutorization(Allow)(parse.json) { (user, json) =>
  * Ok
  * }
  * }
  * }}}
  *
  * @author Giovanni Costagliola
  */
trait AuthenticatedController[User] extends PolicyBasedController {
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
  protected def onUnauthenticated(request: RequestHeader): Result

  /**
    * Default handler for unauthorized requests. Default implementation simply returns an HTTP status 443 (FORBIDDEN).
    * Developer can choose a custom handler for a specific request just by providing their own with the WithAuthenticatation(…) block
    *
    * Mixed class could provide its own implementation by overriding
    */
  protected def onUnauthorizedUser(user: User, request: RequestHeader): Result = Results.Forbidden

  /**
    * Action wrapper that provide authentication features
    */
  object WithAuthentication extends Results {
    /**
      * Action for authenticated users with a custom bodyparser
      *
      * Block can access to the pair (user, request)
      */
    def apply[C](bodyParser: BodyParser[C])(f: (User, Request[C]) => Result): EssentialAction = Security.Authenticated(user, onUnauthenticated) { user =>
      try {
        Action(bodyParser) { request =>
          try {
            f(user, request)
          } catch {
            case e: ValidationException => BadRequest(Json.toJson(e.validationErrors))
          }
        }
      }
    }

    /**
      * Action for authenticated users
      *
      * Block can access to the pair (user, request)
      */
    def apply(f: (User, Request[AnyContent]) => Result): EssentialAction = apply(BodyParsers.parse.anyContent)(f)

    /**
      * Action for authenticated users
      *
      * Block can access to the request
      */
    def apply(f: Request[AnyContent] => Result): EssentialAction = apply(BodyParsers.parse.anyContent)((u: User, r: Request[AnyContent]) => f(r))

    /**
      * Action for authenticated users
      *
      * Block doesn't require user nor request
      */
    def apply(f: => Result): EssentialAction = apply(BodyParsers.parse.anyContent)((u: User, r: Request[AnyContent]) => f)
  }

  /**
    * Action wrapper that provide authorization mechanisms
    *
    * @param rule The authorization rule to validate
    * @param unauthorizedHandler An optional custom handler to manage the unauthorized event
    */
  case class WithAuthorization(rule: AuthorizationRule, unauthorizedHandler: (User, RequestHeader) => Result = onUnauthorizedUser) {
    /**
      * Action for authenticated users with a custom body parser
      *
      * Block can access to the pair (user, request)
      */
    def apply[C](bodyParser: BodyParser[C])(f: (User, Request[C]) => Result): EssentialAction = WithAuthentication(bodyParser) { (user, request) =>
      implicit val authorizationContext = RequestAuthorizationContext //(user, request)
      if (rule.eval) {
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
    def apply(f: Request[AnyContent] => Result): EssentialAction = apply(BodyParsers.parse.anyContent)((u: User, r: Request[AnyContent]) => f(r))

    /**
      * Action for authorization users.
      *
      * Block doesn't require user nor request
      */
    def apply(f: => Result): EssentialAction = apply(BodyParsers.parse.anyContent)((u: User, r: Request[AnyContent]) => f)
  }
}