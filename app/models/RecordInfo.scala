package models

import java.sql.Timestamp

case class RecordInfo (creationTime: Timestamp, modificationTime: Timestamp, version: Int)