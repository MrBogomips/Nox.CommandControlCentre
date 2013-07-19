-- DeviceGroups
ALTER TABLE "device_groups" RENAME "display_name" TO "displayName";
ALTER TABLE "device_groups" RENAME "_ctime" TO "creationTime";
ALTER TABLE "device_groups" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "device_groups" RENAME "_ver" TO "version";
ALTER TABLE "device_groups" RENAME TO "DeviceGroups";

-- DeviceTypes
ALTER TABLE "device_types" RENAME "display_name" TO "displayName";
ALTER TABLE "device_types" RENAME "_ctime" TO "creationTime";
ALTER TABLE "device_types" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "device_types" RENAME "_ver" TO "version";
ALTER TABLE "device_types" RENAME TO "DeviceTypes";

-- SimCards
ALTER TABLE "simcards" RENAME "display_name" TO "displayName";
ALTER TABLE "simcards" RENAME "_ctime" TO "creationTime";
ALTER TABLE "simcards" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "simcards" RENAME "mobile_number" TO "mobileNumber";
ALTER TABLE "simcards" RENAME "carrier_id" TO "carrierId";
ALTER TABLE "simcards" RENAME "_ver" TO "version";
ALTER TABLE "simcards" RENAME TO "Simcards";

-- Users
ALTER TABLE "users" RENAME "display_name" TO "displayName";
ALTER TABLE "users" RENAME "_ctime" TO "creationTime";
ALTER TABLE "users" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "users" RENAME "suspension_reason" TO "suspensionReason";
ALTER TABLE "users" RENAME "_ver" TO "version";
ALTER TABLE "users" RENAME TO "Users";

-- Vehicles
ALTER TABLE "vehicles" RENAME "display_name" TO "displayName";
ALTER TABLE "vehicles" RENAME "_ctime" TO "creationTime";
ALTER TABLE "vehicles" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "vehicles" RENAME "license_plate" TO "licensePlate";
ALTER TABLE "vehicles" RENAME "_ver" TO "version";
ALTER TABLE "vehicles" RENAME TO "Vehicles";

-- Devices
ALTER TABLE "devices" RENAME "display_name" TO "displayName";
ALTER TABLE "devices" RENAME "_ctime" TO "creationTime";
ALTER TABLE "devices" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "devices" RENAME "device_type_id" TO "deviceTypeId";
ALTER TABLE "devices" RENAME "device_group_id" TO "deviceGroupId";
ALTER TABLE "devices" RENAME "vehicle_id" TO "vehicleId";
ALTER TABLE "devices" RENAME "simcard_id" TO "simcardId";
ALTER TABLE "devices" RENAME "_ver" TO "version";
ALTER TABLE "devices" RENAME TO "Devices";

-- Drivers
ALTER TABLE "drivers" RENAME "display_name" TO "displayName";
ALTER TABLE "drivers" RENAME "_ctime" TO "creationTime";
ALTER TABLE "drivers" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "drivers" RENAME "_ver" TO "version";
ALTER TABLE "drivers" RENAME TO "Drivers";

-- VehiclesDrivers
ALTER TABLE "vehicles_drivers" RENAME "_ctime" TO "creationTime";
ALTER TABLE "vehicles_drivers" RENAME "_mtime" TO "modificationTime";
ALTER TABLE "vehicles_drivers" RENAME "begin_assignement" TO "beginAssignement";
ALTER TABLE "vehicles_drivers" RENAME "end_assignement" TO "endAssignement";
ALTER TABLE "vehicles_drivers" RENAME "vehicle_id" TO "vehicleId";
ALTER TABLE "vehicles_drivers" RENAME "driver_id" TO "driverId";
ALTER TABLE "vehicles_drivers" RENAME "_ver" TO "version";
ALTER TABLE "vehicles_drivers" RENAME TO "VehiclesDrivers";

