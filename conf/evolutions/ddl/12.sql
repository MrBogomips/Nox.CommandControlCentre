
# --- !Ups

-- Operators
CREATE SEQUENCE "OperatorsSeq";
CREATE TABLE "Operators" (
	"id"					INT NOT NULL DEFAULT(nextval('"OperatorsSeq"')),
	"name"					text NOT NULL,
	"surname"				text NOT NULL,
	"displayName"			text NOT NULL,
	"enabled"				BOOLEAN NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"OperatorsPK" PRIMARY KEY("id"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime")
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "Operators" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "OperatorsSeq" TO PUBLIC; 

INSERT INTO "Operators" VALUES
	(nextval('"OperatorsSeq"'), 'Pedro', 'Almodovar', 'Almodovar Pedro', true, NOW(), NOW(), 0),
	(nextval('"OperatorsSeq"'), 'Stanley', 'Kubrick', 'Kubrick Stanley', true, NOW(), NOW(), 0),
	(nextval('"OperatorsSeq"'), 'Federico', 'Fellini', 'Fellini Federico', true, NOW(), NOW(), 0);

-- MaintenanceServices
CREATE SEQUENCE "MaintenanceServicesSeq";
CREATE TABLE "MaintenanceServices" (
	"id"					INT NOT NULL DEFAULT(nextval('"MaintenanceServicesSeq"')),
	"name"					text NOT NULL,
	"displayName"			text NOT NULL,
	"description"			text NULL,
	"enabled"				BOOLEAN NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	"odometer"				INT NOT NULL,
	"monthsPeriod"			INT NOT NULL,
	CONSTRAINT				"MaintenanceServicesPK" PRIMARY KEY("id"),
	CONSTRAINT				"MaintenanceServicesNameUQ" UNIQUE("name"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT				"odometer_gt_zero" CHECK ("odometer" > 0),
	CONSTRAINT				"monthsPeriod_gt_zero" CHECK ("monthsPeriod" > 0)
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceServices" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceServicesSeq" TO PUBLIC; 

INSERT INTO "MaintenanceServices" VALUES
	(nextval('"MaintenanceServicesSeq"'), 'AIRBAG', 'Air bag', NULL, true, NOW(), NOW(), 0, 1000, 12),
	(nextval('"MaintenanceServicesSeq"'), 'ENGINEOIL', 'Engine oil', NULL, true, NOW(), NOW(), 0, 1000, 12),
	(nextval('"MaintenanceServicesSeq"'), 'BRAKEOIL', 'Brake oil', NULL, true, NOW(), NOW(), 0, 1000, 12);

-- MaintenanceDuties
CREATE SEQUENCE "MaintenanceDutiesSeq";
CREATE TABLE "MaintenanceDuties" (
	"id"					INT NOT NULL DEFAULT(nextval('"MaintenanceDutiesSeq"')),
	"idVehicle"				INT NOT NULL,
	"idService"				INT NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"MaintenanceDutiesPK" PRIMARY KEY("id"),
	CONSTRAINT				"MaintenanceDutiesAK" UNIQUE("idVehicle", "idService"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT				"MaintenanceDutiesVehicleFK" FOREIGN KEY("idVehicle") REFERENCES "Vehicles",
	CONSTRAINT				"MaintenanceDutiesServiceFK" FOREIGN KEY("idService") REFERENCES "MaintenanceServices"
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceDuties" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceDutiesSeq" TO PUBLIC; 

-- Populate with some data
INSERT INTO "MaintenanceDuties"
	SELECT nextval('"MaintenanceDutiesSeq"'), v.id, s.id, NOW(), NOW(), 0
		FROM "Vehicles" v
		CROSS JOIN "MaintenanceServices" s;

-- MaintenanceActivities
CREATE SEQUENCE "MaintenanceActivitiesSeq";
CREATE TABLE "MaintenanceActivities" (
	"id"					INT NOT NULL DEFAULT(nextval('"MaintenanceActivitiesSeq"')),
	"idVehicle"				INT NOT NULL,
	"idOperator"			INT NOT NULL,
	"odometer"				INT NOT NULL,
	"note"					text NULL,
	"activityStart"			timestamp NOT NULL,
	"activityEnd"			timestamp NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"MaintenanceActivitiesPK" PRIMARY KEY("id"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT 				"ActivityStartEnd" CHECK ("activityStart" >= "activityEnd"),
	CONSTRAINT				"MaintenanceActivitiesVehicleFK" FOREIGN KEY("idVehicle") REFERENCES "Vehicles",
	CONSTRAINT				"MaintenanceActivitiesOperatorFK" FOREIGN KEY("idOperator") REFERENCES "Operators"
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceActivities" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceActivitiesSeq" TO PUBLIC; 

-- MaintenanceActivityOutcomes
CREATE SEQUENCE "MaintenanceActivitiesOutcomesSeq";
CREATE TABLE "MaintenanceActivitiesOutcomes" (
	"id"					INT NOT NULL DEFAULT(nextval('"MaintenanceActivitiesSeq"')),
	"name"					text NOT NULL,
	"displayName"			text NOT NULL,
	"description"			text NULL,
	"enabled"				boolean NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"MaintenanceActivitiesOutcomesPK" PRIMARY KEY("id"),
	CONSTRAINT				"MaintenanceActivitiesOutcomesNameUQ" UNIQUE("name"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime")
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceActivitiesOutcomes" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceActivitiesOutcomesSeq" TO PUBLIC; 

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
	CONSTRAINT				"MaintenanceActivityLogsOutcomeFK" FOREIGN KEY("idOutcome") REFERENCES "MaintenanceActivitiesOutcomes"
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceActivityLogs" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceActivityLogsSeq" TO PUBLIC; 

# --- !Downs
DROP TABLE "MaintenanceActivityLogs";
DROP SEQUENCE "MaintenanceActivityLogsSeq";

DROP TABLE "MaintenanceActivitiesOutcomes";
DROP SEQUENCE "MaintenanceActivitiesOutcomesSeq";

DROP TABLE "MaintenanceActivities";
DROP SEQUENCE "MaintenanceActivitiesSeq";

DROP TABLE "MaintenanceDuties";
DROP SEQUENCE "MaintenanceDutiesSeq";

DROP TABLE "MaintenanceServices";
DROP SEQUENCE "MaintenanceServicesSeq";

DROP TABLE "Operators";
DROP SEQUENCE "OperatorsSeq";
