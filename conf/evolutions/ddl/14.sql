-- MqttLogisticChannels
CREATE SEQUENCE "MqttLogisticChannelsSeq";
CREATE TABLE "MqttLogisticChannels" (
	"id"					INT NOT NULL DEFAULT(nextval('"MqttLogisticChannelsSeq"')),
	"name"					text NOT NULL,
	"displayName"			text NOT NULL,
	"description"			text NULL,
	"enabled"				boolean NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"MqttLogisticChannelsPK" PRIMARY KEY("id"),
	CONSTRAINT				"MqttLogisticChannelsNameUQ" UNIQUE("name"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime")
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MqttLogisticChannels" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MqttLogisticChannelsSeq" TO PUBLIC; 

-- MqttFunctionalChannels
CREATE SEQUENCE "MqttFunctionalChannelsSeq";
CREATE TABLE "MqttFunctionalChannels" (
	"id"					INT NOT NULL DEFAULT(nextval('"MqttFunctionalChannelsSeq"')),
	"name"					text NOT NULL,
	"displayName"			text NOT NULL,
	"description"			text NULL,
	"enabled"				boolean NOT NULL,
	"creationTime"			TIMESTAMP NOT NULL DEFAULT(NOW()),
	"modificationTime"		TIMESTAMP NOT NULL DEFAULT(NOW()),
	"version"				INT NOT NULL DEFAULT(0),
	CONSTRAINT				"MqttFunctionalChannelsPK" PRIMARY KEY("id"),
	CONSTRAINT				"MqttFunctionalChannelsNameUQ" UNIQUE("name"),
	CONSTRAINT 				"mtime_gte_ctime_chk" CHECK ("modificationTime" >= "creationTime")
);
GRANT SELECT, INSERT,  UPDATE, DELETE, TRUNCATE ON TABLE "MqttFunctionalChannels" TO PUBLIC;
GRANT ALL PRIVILEGES ON SEQUENCE "MqttFunctionalChannelsSeq" TO PUBLIC; 