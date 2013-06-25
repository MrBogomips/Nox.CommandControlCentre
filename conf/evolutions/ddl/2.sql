# Users schema

# --- !Ups

CREATE SEQUENCE "devices_id_seq";
CREATE SEQUENCE "device_types_id_seq";
CREATE SEQUENCE "device_groups_id_seq";

CREATE TABLE "device_types" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('device_types_id_seq')),
	name				text NOT NULL UNIQUE,
	display_name		text NOT NULL,
	description			text NULL,
	enabled				BOOLEAN NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime)
);

CREATE TABLE "device_groups" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('device_groups_id_seq')),
	name				text NOT NULL UNIQUE,
	display_name		text NOT NULL,
	description			text NULL,
	enabled				BOOLEAN NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime)
);

CREATE  TABLE "devices" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('devices_id_seq')),
	name				text NOT NULL UNIQUE,
	display_name		text NOT NULL,
	description			text NULL,
	enabled				BOOLEAN NOT NULL,
	device_type_id		INT NOT NULL,
	device_group_id		INT NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime),
	CONSTRAINT defice_type_fk FOREIGN KEY(device_type_id) REFERENCES "device_types",
	CONSTRAINT defice_group_fk FOREIGN KEY(device_group_id) REFERENCES "device_groups"
);

GRANT ALL PRIVILEGES ON SEQUENCE "device_types_id_seq" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "device_groups_id_seq" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "devices_id_seq" TO PUBLIC;

GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "devices" TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "device_groups" TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "device_types" TO PUBLIC;

# --- !Downs
DROP TABLE IF EXISTS "devices";
DROP TABLE IF EXISTS "device_groups";
DROP TABLE IF EXISTS "device_types";
DROP SEQUENCE IF EXISTS "devices_id_seq";
DROP SEQUENCE IF EXISTS "device_groups_id_seq";
DROP SEQUENCE IF EXISTS "device_types_id_seq";
