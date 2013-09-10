# Users schema

# --- !Ups

CREATE SEQUENCE "vehicle_types_id_seq";

CREATE TABLE "VehicleTypes" (
	"id"					INT NOT NULL PRIMARY KEY DEFAULT(nextval('device_types_id_seq')),
	"name"				text NOT NULL UNIQUE,
	"displayName"			text NOT NULL,
	"description"			text NULL,
	"enabled"				BOOLEAN NOT NULL,
	"creationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"	TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK ("modificationTime" >= "creationTime")
);

GRANT ALL PRIVILEGES ON SEQUENCE "vehicle_types_id_seq" TO PUBLIC;

GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "VehicleTypes" TO PUBLIC;

ALTER TABLE "Vehicles" 
	ADD "vehicleTypeId" INT NULL;
ALTER TABLE "Vehicles" 
	ADD CONSTRAINT "vehicle_type_fk" 
	FOREIGN KEY ("vehicleTypeId")
	REFERENCES "VehicleTypes";

# --- !Downs
DROP TABLE IF EXISTS "VehicleTypes";
DROP SEQUENCE IF EXISTS "vehicle_types_id_seq";
ALTER TABLE "Vehicles" 
	DROP CONSTRAINT "vehicle_type_fk";
ALTER TABLE "Vehicles" 
	DROP COLUMN "vehicleTypeId";
