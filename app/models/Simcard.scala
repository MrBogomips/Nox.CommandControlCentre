package models

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
  with ValidationRequired

case class SimcardPersisted(id: Int, imei: String, displayName0: Option[String], description: Option[String], enabled: Boolean, mobileNumber: String, carrierId: Int, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends SimcardTrait
  with Persistable {
  def this(id: Int, imei: String, displayName0: Option[String], description: Option[String], enabled: Boolean, mobileNumber: String, carrierId: Int, version: Int) = this(id, imei, displayName0, description, enabled, mobileNumber, carrierId, new Timestamp(0), new Timestamp(0), version)
}

object Simcards
  extends Table[SimcardPersisted]("simcards")
  with Backend {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def imei = column[String]("imei")
  def displayName = column[String]("display_name")
  def description = column[String]("description", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def mobileNumber = column[String]("mobile_number")
  def carrierId = column[Int]("carrier_id")

  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  //  def deviceTypeFk = foreignKey("device_type_fk", deviceTypeId, DeviceTypes)(_.id)
  //  def deviceGroupFk = foreignKey("device_group_fk", deviceGroupId, DeviceGroups)(_.id)
  //  def vehicleFk = foreignKey("vehicle_fk", vehicleId, Vehicles)(_.id)

  def * = id ~ imei ~ displayName.? ~ description.? ~ enabled ~ mobileNumber ~ carrierId ~ _ctime ~ _mtime ~ _ver <> (SimcardPersisted, SimcardPersisted.unapply _)

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
    else if (errMessage.contains("mtime_gte_ctime_chk"))
      throw new ValidationException(e, "creationTime,modificationTime", "Not in the correct sequence")
    else
      throw e;
  }

  def find(enabled: Option[Boolean] = None): Seq[SimcardPersisted] = db withSession {
    val qy = enabled match {
      case None     => for { s <- Simcards } yield s
      case Some(en) => for { s <- Simcards if (s.enabled === en) } yield s
    }

    qy.list
  }

  def findById(id: Int): Option[SimcardPersisted] = db withSession {
    val qy = for { s <- Simcards if (s.id === id) } yield s

    qy.firstOption
  }

  def findByImei(imei: String): Option[SimcardPersisted] = db withSession {
    val qy = for { s <- Simcards if (s.imei === imei) } yield s

    qy.firstOption
  }

  def findByMobileNumber(number: String): Option[SimcardPersisted] = db withSession {
    val qy = for { s <- Simcards if (s.mobileNumber === number) } yield s

    qy.firstOption
  }

  def insert(uobj: Simcard): Int = WithValidation(uobj) { obj =>
    db withSession {

      val sql = sql"""
    INSERT INTO #$tableName  (
    		imei, 
    		display_name,
    		description,
    		enabled,
    		mobile_number,
    		carrier_id, 
    		_ctime,
    		_mtime,
    		_ver
    ) 
    VALUES (
    		${obj.imei},
    		${obj.displayName},
    		${obj.description},
    		${obj.enabled},
    		${obj.mobileNumber},
    		${obj.carrierId},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """
      executeSql(BackendOperation.INSERT, s"$tableName $obj", sql.as[Int]) { _.first }
    }
  }
  def update(uobj: SimcardPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET imei = ${obj.imei},
	           display_name = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   mobile_number = ${obj.mobileNumber},
	           carrier_id = ${obj.carrierId},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${obj.id}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def updateWithVersion(uobj: SimcardPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET imei = ${obj.imei},
	           display_name = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   mobile_number = ${obj.mobileNumber},
	           carrier_id = ${obj.carrierId},
	           _mtime = NOW(),
	           _ver = _ver + 1
		 WHERE id = ${obj.id}
		   AND _ver = ${obj.version}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def delete(obj: SimcardPersisted): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean = WithValidation {
    db withSession {
      val sql = sqlu"DELETE FROM #$tableName WHERE id = ${id}"
      executeDelete(s"$tableName $id", sql) == 1
    }
  }
}




