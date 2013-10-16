package models

import patterns.models._
import play.api.Logger
import play.api.db._
import play.api.Play.current
import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation
import org.postgresql.util.PSQLException
import java.util.Date

/**
 * The common trait exposed by a named entity
 */
trait NamedEntityTrait extends Validatable {
  val name: String
  val displayName0: Option[String]
  val displayName: String = displayName0.map(v => v).getOrElse(name)
  val description: Option[String]
  val enabled: Boolean

  def validate {
    validateMinLength("name", name, 3)
    validateMinLength("displayName", displayName, 3)
  }
}

/**
 * The model associated with the trait
 */
trait NamedEntity[A <: NamedEntityTrait] extends NamedEntityTrait with Model[A]

/**
 * The persisted entity associated with the trait
 */
trait NamedEntityPersisted[A <: NamedEntity[_]] extends NamedEntityTrait with Persisted[A]


// TODO: implement a generic DAO that supports the basic CRUD operations
/**
 * Basic Data Access Object that prvide CRUD operations to NamedEntities like
 */
abstract class NamedEntityCrudTable[TRAIT <: NamedEntityTrait, MODEL <: NamedEntity[TRAIT], PERSISTED <: NamedEntityPersisted[MODEL]](tableName: String, nameUniqueConstraint: String)
  extends EnabledEntityCrudTable[TRAIT, MODEL, PERSISTED](tableName) {

  def name = column[String]("name")
  def displayName = column[String]("displayName", O.Nullable)
  def description = column[String]("description", O.Nullable)
  
  def forInsert: scala.slick.lifted.ColumnBase[MODEL]
  
   /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  protected implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains(nameUniqueConstraint))
      throw new ValidationException(e, "name", "Already in use")
    else
      throw e;
  }
  
  def findByName(name: String): Option[PERSISTED] = db withSession {
    val qy = for { dt <- this if (dt.name === name) } yield dt
    qy.firstOption
  }

  def update(uobj: PERSISTED): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id) }
        yield d.name ~ d.displayName ~ d.description.? ~ d.enabled ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.displayName, obj.description, obj.enabled, now, obj.version + 1)) == 1
    }
  }
  def updateWithVersion(uobj: PERSISTED): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id && d.version === obj.version) }
        yield d.name ~ d.displayName ~ d.description.? ~ d.enabled ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.displayName, obj.description, obj.enabled, now, obj.version + 1)) == 1
    }
  }
}
