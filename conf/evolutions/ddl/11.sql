
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

-- MaintenanceServices
CREATE SEQUENCE "MaintenanceServicesSeq";
CREATE TABLE "MaintenanceServices" (
	"id"					INT NOT NULL DEFAULT(nextval('"MaintenanceServicesSeq"')),
	"name"					text NOT NULL,
	"surname"				text NOT NULL,
	"displayName"			text NOT NULL,
	"enabled"				BOOLEAN NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	"odometer"				INT NOT NULL,
	"monthsPeriod"			INT NOT NULL,
	CONSTRAINT				"MaintenanceServicesPK" PRIMARY KEY("id"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT				"odometer_gt_zero" CHECK ("odometer" > 0),
	CONSTRAINT				"monthsPeriod_gt_zero" CHECK ("monthsPeriod" > 0)
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceServices" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceServicesSeq" TO PUBLIC; 

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
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT				"MaintenanceDutiesVehicleFK" FOREIGN KEY("idVehicle") REFERENCES "Vehicles",
	CONSTRAINT				"MaintenanceDutiesServiceFK" FOREIGN KEY("idService") REFERENCES "MaintenanceServices"
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceDuties" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceDutiesSeq" TO PUBLIC; 

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
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime"),
	CONSTRAINT				"MaintenanceActivityLogsActivityFK" FOREIGN KEY("idActivity") REFERENCES "MaintenanceActivities",
	CONSTRAINT				"MaintenanceActivityLogsOutcomeFK" FOREIGN KEY("idActivity") REFERENCES "MaintenanceActivitiesOutcomes"
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MaintenanceActivityLogs" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MaintenanceActivityLogsSeq" TO PUBLIC; 
