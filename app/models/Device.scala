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
import models.Backend

case class Device private[models] (id: Option[Int], name: String, recordInfo: Option[RecordInfo]) extends MaybePersisted {
  def this(name: String) = this(None, name, None)
  
  require(name.length >5, "device name....")
}

object Devices extends Table[Device]("devices") with Backend {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")
  
  def * = id.? ~ name ~ _ctime ~ _mtime ~ _ver <> (
      {t => Device(t._1, t._2, Some(RecordInfo(t._3, t._4, t._5)))}, 
      {d:Device => Some(d.id, d.name, d.recordInfo.get.creationTime, d.recordInfo.get.modificationTime, d.recordInfo.get.version)}
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