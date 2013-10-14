-- FIX an inchoerence with naming conventions

DROP TABLE "MaintenanceActivityLogs";
DROP SEQUENCE "MaintenanceActivityLogsSeq";

DROP TABLE "MaintenanceActivitiesOutcomes";
DROP SEQUENCE "MaintenanceActivitiesOutcomesSeq";

-- MaintenanceActivityOutcomes
CREATE SEQUENCE "MaintenanceActivityOutcomesSeq";
CREATE TABLE "MaintenanceActivityOutcomes" (
	"id"					INT NOT NULL DEFAULT(nextval('"MaintenanceActivityOutcomesSeq"')),
	"name"					text NOT NULL,
	"displayName"			text NOT NULL,
	"description"			text NULL,
	"enabled"				boolean NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"MaintenanceActivityOutcomesPK" PRIMARY KEY("id"),
	CONSTRAINT				"MaintenanceActivityOutcomesNameUQ" UNIQUE("name"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime")
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceActivityOutcomes" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceActivityOutcomesSeq" TO PUBLIC; 

-- MaintenanceActivityLogs
CREATE SEQUENCE "MaintenanceActivityLogsSeq";
CREATE TABLE "MaintenanceActivityLogs" (
	"id"					INT NOT NULL DEFAULT(nextval('"MaintenanceActivityLogsSeq"')),
	"idActivity"			INT NOT NULL,
	"idService"				INT NOT NULL,
	"idOutcome"				INT NOT NULL,
	"note"					text NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"MaintenanceActivityLogsPK" PRIMARY KEY("id"),
	CONSTRAINT				"MaintenanceActivityLogsAK" UNIQUE("idActivity", "idService"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT				"MaintenanceActivityLogsActivityFK" FOREIGN KEY("idActivity") REFERENCES "MaintenanceActivities",
	CONSTRAINT				"MaintenanceActivityLogsServiceFK" FOREIGN KEY("idService") REFERENCES "MaintenanceServices",
	CONSTRAINT				"MaintenanceActivityLogsOutcomeFK" FOREIGN KEY("idOutcome") REFERENCES "MaintenanceActivityOutcomes"
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceActivityLogs" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceActivityLogsSeq" TO PUBLIC; 