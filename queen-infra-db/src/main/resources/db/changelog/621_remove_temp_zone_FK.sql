--liquibase formatted sql

--changeset davdarras:621-0
ALTER TABLE public."interrogation_temp_zone" DROP CONSTRAINT "FK_interrogation";

