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
  extends Table[OperatorPersisted]("Operators")
  with Backend
  with NameEntityCrudOperations[OperatorTrait, Operator, OperatorPersisted] {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def surname = column[String]("surname")
  def displayName = column[String]("displayName")
  def enabled = column[Boolean]("enabled")
  def creationTime = column[Timestamp]("creationTime")
  def modificationTime = column[Timestamp]("modificationTime")
  def version = column[Int]("version")

  def * = id ~ name ~ surname ~ displayName.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (OperatorPersisted, OperatorPersisted.unapply _)
  def forInsert = name ~ surname ~ displayName.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => Operator(t._1, t._2, t._3, t._4) },
    { (o: Operator) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.name, o.surname, Some(o.displayName), o.enabled, now, now, 0))
      }
    })

  private implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    //if (errMessage.contains("vehicle_name_key"))
    //  throw new ValidationException(e, "name", "Already in use")
    //else
    throw e;
  }

  def qyFindById(id: Int) = (for { d <- Drivers if (d.id === id) } yield d)

  def find(enabled: Option[Boolean] = None): Seq[OperatorPersisted] = db withSession {
    val qy = enabled match {
      case None     => for { d <- Operators } yield d
      case Some(en) => for { d <- Operators if (d.enabled === en) } yield d
    }
    qy.list
  }

  def findById(id: Int): Option[OperatorPersisted] = db withSession {
    val qy = for { d <- Operators if (d.id === id) } yield d
    qy.firstOption
  }

  def deleteById(id: Int): Boolean = db withSession {
    val qy = for { d <- Operators if (d.id === id) } yield d
    qy.delete == 1
  }

  def findByName(name: String): Option[OperatorPersisted] = db withSession {
    val qy = for { d <- Operators if (d.name === name) } yield d
    qy.firstOption
  }

  def insert(uobj: models.Operator): Int = WithValidation(uobj) { obj =>
    db withSession {
      Operators.forInsert returning Operators.id insert obj
    }
  }
  def update(uobj: models.OperatorPersisted): Boolean = WithValidation(uobj) { obj =>
    val qy = for { d <- Operators if (d.id === id) } yield d
    qy.update(obj) == 1
  }
  def updateWithVersion(uobj: models.OperatorPersisted): Boolean = WithValidation(uobj) { obj =>
    val qy = for { d <- Operators if (d.id === id && d.version === version) } yield d
    qy.update(obj) == 1
  }
}


