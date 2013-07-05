package models

case class ValidationException(validationErrors: Seq[ValidationError]) extends RuntimeException

/*
 * Represents a validation error
 */
case class ValidationError(key: String, conditions: Set[String])

/*
 * Provides validation capabilities to a model
 */
trait Validatable {
  /*
   * Each concrete class must provide the validation logic
   */
  def validate: Seq[ValidationError]

  lazy val validationErrors = validate
  lazy val isValid = validationErrors.isEmpty
  
  import collection.mutable.{ HashMap, MultiMap, Set }
  protected val errorsAccumulator = new HashMap[String, Set[String]] with MultiMap[String, String]
  protected def getValidationErrors: Seq[ValidationError] = {
    ???
  }

  def requireValidation = if (!isValid) throw ValidationException(validationErrors)
}

trait ValidationRequired extends Validatable {
  requireValidation
}

/*
 * Utility object to catch and chain validation errors
 */
object WithValidation {
  def apply[A <: Validatable](f: => A): Either[Seq[ValidationError], A] = try {
    val v = f
    if (v.isValid)
    	Right(v)
    else
      Left(v.validationErrors)
  } catch {
    case e: ValidationException => Left(e.validationErrors)
  }
}