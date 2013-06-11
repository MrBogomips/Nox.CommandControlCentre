# Users schema

# --- !Ups

--CREATE SCHEMA "security";

CREATE SEQUENCE "user_id_seq";

CREATE TABLE "user" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('user_id_seq')),
	login				text NOT NULL UNIQUE,
	password			text NULL,
	status				text NOT NULL,
	suspension_reason	text,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime),
	CONSTRAINT suspension_reason_set_chk CHECK (status != 'suspended' AND suspension_reason IS NULL)
);

--GRANT USAGE ON SCHEMA "security" TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON TABLE "user" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "user_id_seq" TO PUBLIC; 
 

# --- !Downs
DROP TABLE IF EXISTS "user";
DROP DOMAIN IF EXISTS "login";
DROP TYPE IF EXISTS "user_status";
DROP TYPE IF EXISTS "suspension_reason";
DROP SEQUENCE IF EXISTS "user_id_seq";
