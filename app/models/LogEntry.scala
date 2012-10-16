package models

import collection.mutable.ListBuffer

case class LogEntry(
	id: Long, 
	severity: String, 
	category: String, 
	deviceId: String,
	statusCode: String,
	actions: String,
	data: String)

object LogEntry {
	def all(): List[LogEntry] = {
		val lb = new ListBuffer[LogEntry]
		lb += getRandom
		lb += getRandom
		lb += getRandom
		lb toList
	}

	def create(label: String) {}

	def delete(id: Long) {}

	def getRandom(): LogEntry = {
		LogEntry(1, "Warning", "Diagnostic", "12345", "W0001", "Actions", "XXXX")
	}
}

