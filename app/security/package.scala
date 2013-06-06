package object security {
	val Acl = Any
  
	val AclDeviceProvisioning = Acl(
			CanUse("device.create"),
			CanUse("device.destroy")
	)
	
	val AclDeviceUpdate = Acl(
			CanUse("device.update")
	)
	
	val AclDeviceAdmin = Acl(
			AclDeviceProvisioning,
			AclDeviceUpdate
	)
	
	val AclNumeroUno = Allow
}