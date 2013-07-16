

CREATE SEQUENCE "simcard_id_seq";

CREATE  TABLE "simcards" (
	id					INT NOT NULL DEFAULT(nextval('simcard_id_seq')),
	imei				text NOT NULL,
	display_name		text NOT NULL,
	description			text NULL,
	enabled				BOOLEAN NOT NULL,
	mobile_number		text NOT NULL,
	carrier_id			INT NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
	
	CONSTRAINT simcards_pk PRIMARY KEY(id),
	CONSTRAINT mtime_gte_ctime_chk CHECK (_mtime >= _ctime), 
	CONSTRAINT simcards_imei_ak UNIQUE(imei),
	CONSTRAINT simcards_mobile_number_ak UNIQUE(mobile_number)
);

--GRANT USAGE ON SCHEMA "security" TO PUBLIC;--
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "simcards" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "simcard_id_seq" TO PUBLIC; 
 

# --- !Downs
DROP TABLE IF EXISTS "simcards";
DROP SEQUENCE IF EXISTS "simcard_id_seq";



