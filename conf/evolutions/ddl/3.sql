# Users schema

# --- !Ups

CREATE SEQUENCE "vehicles_id_seq";

CREATE TABLE "vehicles" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('vehicles_id_seq')),
	name				text NOT NULL UNIQUE,
	display_name		text NOT NULL,
	description			text NULL,
	enabled				BOOLEAN NOT NULL,
	model				text NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime)
);

GRANT ALL PRIVILEGES ON SEQUENCE "vehicles_id_seq" TO PUBLIC;

GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "vehicles" TO PUBLIC;

ALTER TABLE "devices"
	ADD "vehicle_id" INT;
	
ALTER TABLE "devices"
	ADD CONSTRAINT "vehicle_id_fk"
	FOREIGN KEY ("vehicle_id")
	REFERENCES "vehicles";

# --- !Downs
ALTER TABLE "devices" DROP CONSTRAINT "vehicle_id_fk";
ALTER TABLE "devices" DROP COLUMN "vehicle_id";
DROP TABLE IF EXISTS "vehicles";
DROP SEQUENCE IF EXISTS "vehicles_id_seq";
