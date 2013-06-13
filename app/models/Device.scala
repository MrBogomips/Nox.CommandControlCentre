package models
/*
import play.api.Logger
import play.api.db._
import play.api.Play.current
import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation
import models.Backend

case class Device private[models] (id: Int, name: String, recordInfo: RecordInfo) extends Persisted[Device] {
  
  require(name.length >5, "device name....")
}

object Devices extends Table[Device]("devices") with Backend {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name", O.Nullable)
  def description = column[String]("display_name", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def deviceTypeId = column[Int]("device_type_id")
  def deviceGroupId = column[Int]("device_group_id")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")
  
  def * = id ~ name ~ _ctime ~ _mtime ~ _ver <> (
      {t => Device(t._1, t._2, RecordInfo(t._3, t._4, t._5))}, 
      {d:Device => Some(d.id, d.name, d.recordInfo.creationTime, d.recordInfo.modificationTime, d.recordInfo.version)}
  )

  /**
   * Retrieve all devices.
   */
  def findAll: Seq[Device] = db withSession { Query(Devices).list }
  
  /**
   * Create a Device.
   *
   * @return The device id
   */
  //def create(device: Device): Int = db withSession { session =>
    //val q = Devices.forInsert returning Device.id
    //q insert device
    
  //}
}
*/