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
import org.joda.time.DateTime
import utils.Converter._

trait SimcardTrait extends Validatable {

  val imei: String
  val displayName0: Option[String]
  val displayName: String = displayName0.map(v => v).getOrElse(imei)
  val description: Option[String]
  val enabled: Boolean
  val mobileNumber: String
  val carrierId: Int

  def validate {
    validateMinLength("displayName", displayName, 3)
    validateMinLength("mobileNumber", mobileNumber, 5)
    validateMinValue("carrierId", carrierId, 1)
    validateImei("imei", imei)
  }
}

case class Simcard(imei: String, displayName0: Option[String], description: Option[String], enabled: Boolean, mobileNumber: String, carrierId: Int)
  extends SimcardTrait
  with Model[SimcardTrait]

case class SimcardPersisted(id: Int, imei: String, displayName0: Option[String], description: Option[String], enabled: Boolean, mobileNumber: String, carrierId: Int, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends SimcardTrait
  with Persisted[Simcard] {
  def this(id: Int, imei: String, displayName0: Option[String], description: Option[String], enabled: Boolean, mobileNumber: String, carrierId: Int, version: Int) =
    this(id, imei, displayName0, description, enabled, mobileNumber, carrierId, new Timestamp(0), new Timestamp(0), version)
}

object Simcards
  extends EnabledEntityCrudTable[SimcardTrait, Simcard, SimcardPersisted]("Simcards") {

  def imei = column[String]("imei")
  def displayName = column[String]("displayName")
  def description = column[String]("description", O.Nullable)
  def mobileNumber = column[String]("mobileNumber")
  def carrierId = column[Int]("carrierId")

  def * = id ~ imei ~ displayName.? ~ description.? ~ enabled ~ mobileNumber ~ carrierId ~ creationTime ~ modificationTime ~ version <> (SimcardPersisted, SimcardPersisted.unapply _)
  def forInsert = imei ~ displayName.? ~ description.? ~ enabled ~ mobileNumber ~ carrierId ~ creationTime ~ modificationTime ~ version <> (
    { t => Simcard(t._1, t._2, t._3, t._4, t._5, t._6) },
    { (o: Simcard) =>
      {
        import java.util.Date
        val now = new Timestamp(new Date().getTime())
        Some((o.imei, Some(o.displayName), o.description, o.enabled, o.mobileNumber, o.carrierId, now, now, 0))
      }
    })

  /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("simcards_imei_ak"))
      throw new ValidationException(e, "imei", "Already in use")
    else if (errMessage.contains("simcards_mobile_number_ak"))
      throw new ValidationException(e, "mobileNumber", "Already in use")
    else if (errMessage.contains("mtime_gte_creationTime_chk"))
      throw new ValidationException(e, "creationTime,modificationTime", "Not in the correct sequence")
    else
      throw e;
  }

  def findByImei(imei: String): Option[SimcardPersisted] = db withSession {
    val qy = for { s <- Simcards if (s.imei === imei) } yield s

    qy.firstOption
  }

  def findByMobileNumber(number: String): Option[SimcardPersisted] = db withSession {
    val qy = for { s <- Simcards if (s.mobileNumber === number) } yield s

    qy.firstOption
  }
  // TODO: reimplement avoiding explicit SQL
  def update(uobj: SimcardPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET imei = ${obj.imei},
	           "displayName" = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   "mobileNumber" = ${obj.mobileNumber},
	           "carrierId" = ${obj.carrierId},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${obj.id}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  // TODO: reimplement avoiding explicit SQL
  def updateWithVersion(uobj: SimcardPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET imei = ${obj.imei},
	           "displayName" = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   "mobileNumber" = ${obj.mobileNumber},
	           "carrierId" = ${obj.carrierId},
	           "modificationTime" = NOW(),
	           version = version + 1
		 WHERE id = ${obj.id}
		   AND version = ${obj.version}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
}




