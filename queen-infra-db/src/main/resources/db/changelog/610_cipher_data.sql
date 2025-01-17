--liquibase formatted sql

--changeset davdarras:610-0
ALTER TABLE data add column encrypted integer;
UPDATE data set encrypted=0;

--changeset davdarras:610-1 context:ciphered-data
CREATE EXTENSION IF NOT EXISTS pgcrypto;
ALTER TABLE data add column tempdata bytea;

--changeset davdarras:610-2 context:ciphered-data
UPDATE data SET tempdata = pgp_sym_encrypt(value::text, current_setting('data.encryption.key'), 's2k-count=65536');
UPDATE data set encrypted=1;

--changeset davdarras:610-3 context:ciphered-data
ALTER TABLE data DROP COLUMN value;

--changeset davdarras:610-4 context:ciphered-data
ALTER TABLE data RENAME COLUMN tempdata TO value;