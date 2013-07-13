# --- !Ups
ALTER TABLE "devices"
	ADD CONSTRAINT "devices_vehicles_fk" FOREIGN KEY (vehicle_id) REFERENCES vehicles;

# --- !Downs
ALTER TABLE "devices"
	DROP CONSTRAINT "devices_vehicles_fk";