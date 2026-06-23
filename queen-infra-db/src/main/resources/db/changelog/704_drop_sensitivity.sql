--liquibase formatted sql

--changeset davdarras:704-1

ALTER TABLE campaign DROP COLUMN sensitivity;
