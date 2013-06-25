package models

object InitDB {
  
  def initUsers = {
	val admin = new User("administrator", ClearPassword("Rumba001"))
	val giovanni = new User("giovanni", ClearPassword("Giovanni001"))
	val roberto = new User("roberto", ClearPassword("Roberto001"))
	
	Users.insert(admin)
	Users.insert(giovanni)
	Users.insert(roberto)
  }

  def deviceTypes = {
    val dt1 = new DeviceType("tipo1")
    val dt2 = new DeviceType("tipo2")
    val dt3 = new DeviceType("tipo3")

    DeviceTypes.insert(dt1)
    DeviceTypes.insert(dt2)
    DeviceTypes.insert(dt3)
  }

  def deviceGroups = {
    val dg1 = new DeviceGroup("group1")
    val dg2 = new DeviceGroup("group2")
    val dg3 = new DeviceGroup("group3")

    DeviceGroups.insert(dg1)
    DeviceGroups.insert(dg2)
    DeviceGroups.insert(dg3)
  }

  def devices = {
    // Creating a new Device without using persisted types and groups
    val dt1 = new DeviceType("tipo1")
    val dg1 = new DeviceGroup("group1")
    val d1 = new Device("device1", dt1, dg1)
    
    // Creating a new Device using persisted types and groups
    val dt2 = DeviceTypes.findByName("tipo1").get
    val dg2 = DeviceGroups.findByName("group1").get
    val d2 = new Device("device2", dt2, dg2)
    
    Devices.insert(d1)
    Devices.insert(d2)
  }
}