# Users schema

# --- !Ups

CREATE SCHEMA "security";

CREATE DOMAIN "security"."login" AS TEXT;
CREATE TYPE "security"."user_status" AS ENUM ('active', 'suspended', 'inactive');
CREATE TYPE "security"."suspension_reason" AS ENUM ('too_many_login_attempt');

CREATE TABLE "security"."user" (
	user_id			SERIAL NOT NULL PRIMARY KEY,
	login			"security"."login" NOT NULL UNIQUE,
	password		text NOT NULL,
	status			"security"."user_status" NOT NULL,
	suspension_reason	"security"."suspension_reason",
	ctime			TIMESTAMP NOT NULL DEFAULT(NOW()),
	mtime			TIMESTAMP NOT NULL DEFAULT(NOW()),
	CONSTRAINT mtime_gte_ctime_chk CHECK (mtime >= ctime),
	CONSTRAINT suspension_reason_set_chk CHECK (status != 'suspended' AND suspension_reason IS NULL)
);

GRANT USAGE ON SCHEMA "security" TO PUBLIC;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE ON ALL TABLES IN SCHEMA "security" TO PUBLIC;


# --- !Downs
DROP SCHEMA IF EXISTS "security" CASCADE; 