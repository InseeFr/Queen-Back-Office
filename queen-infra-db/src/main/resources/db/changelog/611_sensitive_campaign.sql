--liquibase formatted sql

--changeset davdarras:611-0
ALTER TABLE campaign add column sensitivity varchar(255);
UPDATE campaign set sensitivity='NORMAL';