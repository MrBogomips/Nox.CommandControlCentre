# --- !Ups

CREATE SEQUENCE "drivers_id_seq";
CREATE SEQUENCE "vehicles_drivers_id_seq";

CREATE TABLE "drivers" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('drivers_id_seq')),
	name				text NOT NULL,
	surname				text NOT NULL,
	display_name		text NOT NULL,
	enabled				BOOLEAN NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime)
);

CREATE TABLE "vehicles_drivers" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('vehicles_drivers_id_seq')),
	vehicle_id			INT NOT NULL,
	driver_id			INT NOT NULL,
	begin_assignement	TIMESTAMP NOT NULL,
	end_assignement		TIMESTAMP NOT NULL,
	enabled				BOOLEAN NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime),
	CONSTRAINT assignement_chk CHECK (end_assignement >= begin_assignement),
	CONSTRAINT vehicles_drivers_vehicle_fk FOREIGN KEY(vehicle_id) REFERENCES "vehicles",
	CONSTRAINT vehicles_drivers_driver_fk FOREIGN KEY(driver_id) REFERENCES "drivers"
);

GRANT ALL PRIVILEGES ON SEQUENCE "drivers_id_seq" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "vehicles_drivers_id_seq" TO PUBLIC;

GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "drivers" TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "vehicles_drivers" TO PUBLIC;

# --- !Downs

DROP TABLE vehicles_drivers;
DROP TABLE drivers;
DROP SEQUENCE vehicles_drivers_id_seq;
DROP SEQUENCE drivers_id_seq;
