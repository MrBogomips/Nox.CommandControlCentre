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

trait OperatorTrait extends PersonTrait

case class Operator(name: String, surname: String, displayName0: Option[String], enabled: Boolean)
  extends OperatorTrait
  with Model[OperatorTrait] {
  def this(name: String, surname: String, enabled: Boolean = true) =
    this(name, surname, None, enabled)
}

case class OperatorPersisted(id: Int, name: String, surname: String, displayName0: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends OperatorTrait
  with Persisted[Operator]

object Operators
  extends EnabledEntityCrudTable[OperatorTrait, Operator, OperatorPersisted]("Operators") {

  // implement only specific fields
  def name = column[String]("name")
  def surname = column[String]("surname")
  def displayName = column[String]("displayName")

  def * = id ~ name ~ surname ~ displayName.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (OperatorPersisted, OperatorPersisted.unapply _)
  def forInsert = name ~ surname ~ displayName.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => Operator(t._1, t._2, t._3, t._4) },
    { (o: Operator) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.name, o.surname, Some(o.displayName), o.enabled, now, now, 0))
      }
    })

  override implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    //if (errMessage.contains("vehicle_name_key"))
    //  throw new ValidationException(e, "name", "Already in use")
    //else
    throw e;
  }

  def qyFindById(id: Int) = (for { d <- Drivers if (d.id === id) } yield d)

  def update(uobj: models.OperatorPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id) } 
        yield d.name ~ d.surname ~ d.displayName ~ d.enabled ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.surname, obj.displayName, obj.enabled, now, obj.version + 1)) == 1
    }
  }
  def updateWithVersion(uobj: models.OperatorPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === id && d.version === obj.version) } 
        yield d.name ~ d.surname ~ d.displayName ~ d.enabled ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.surname, obj.displayName, obj.enabled, now, obj.version + 1)) == 1
    }
  }
}


