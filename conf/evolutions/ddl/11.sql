# --- !Ups
-- Device Manager clustering


CREATE SEQUENCE "DeviceManagersSeq";

CREATE  TABLE "DeviceManagers" (
	"id"					INT NOT NULL DEFAULT(nextval('"DeviceManagersSeq"')),
	"name"					text NOT NULL,
	"displayName"			text NOT NULL,
	"description"			text NULL,
	"enabled"				BOOLEAN NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT "DeviceManagersPK" PRIMARY KEY ("id"),
	CONSTRAINT "DeviceManagersAK" UNIQUE ("name"),
	CONSTRAINT mtime_gte_ctime_chk CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT version_not_negative CHECK ("version" >= 0)
);

GRANT ALL PRIVILEGES ON SEQUENCE "DeviceManagersSeq" TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "DeviceManagers" TO PUBLIC;

ALTER TABLE "Devices"
	ADD COLUMN "deviceManagerId" INT;
	
ALTER TABLE "Devices"
	ADD CONSTRAINT "DevicesToDeviceManagersFK"
	FOREIGN KEY ("deviceManagerId")
	REFERENCES "DeviceManagers";
	
-- Add some data
INSERT INTO "DeviceManagers" ("id", "name", "displayName", "description", "enabled", "creationTime", "modificationTime", "version") VALUES
	(nextval('"DeviceManagersSeq"'), 'CENTRAL', 'CENTRAL', NULL, true, current_timestamp, current_timestamp, 0),
	(nextval('"DeviceManagersSeq"'), 'CENTRAL2', 'CENTRAL TWO', NULL, true, current_timestamp, current_timestamp, 0);
	
UPDATE "Devices"
	SET "deviceManagerId" = (SELECT "id" FROM "DeviceManagers" WHERE "name" = 'CENTRAL');

# --- !Downs
ALTER TABLE "Devices"
	DROP CONSTRAINT "DevicesToDeviceManagersFK";

DROP TABLE "DeviceManagers";

DROP SEQUENCE "DeviceManagersSeq";
