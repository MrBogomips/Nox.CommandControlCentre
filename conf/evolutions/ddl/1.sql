# Users schema

# --- !Ups

--CREATE SCHEMA "security";


CREATE SEQUENCE "users_id_seq";

CREATE  TABLE "users" (
	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('users_id_seq')),
	login				text NOT NULL UNIQUE, 
	display_name		text NOT NULL,
	password			text NULL,
	status				text NOT NULL,
	suspension_reason	text,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime),
	CONSTRAINT suspension_reason_set_chk CHECK (status != 'suspended' AND suspension_reason IS NULL)
);

--GRANT USAGE ON SCHEMA "security" TO PUBLIC;--
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "users" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "users_id_seq" TO PUBLIC; 
 

# --- !Downs
DROP TABLE IF EXISTS "users";
DROP SEQUENCE IF EXISTS "users_id_seq";
