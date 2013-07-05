# --- !Ups
ALTER TABLE "vehicles"
	ADD license_plate TEXT NOT NULL DEFAULT('');
	
ALTER TABLE "vehicles"
	ALTER COLUMN license_plate DROP DEFAULT;

# --- !Downs
ALTER TABLE "vehicles"
	DROP COLUMN license_plate;