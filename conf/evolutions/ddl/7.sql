# --- !Ups
ALTER TABLE "devices"
	ADD "imei" TEXT;

# --- !Downs
ALTER TABLE "devices"
	DROP column  "imei";