--liquibase formatted sql

--changeset davdarras:620-0
ALTER TABLE public.survey_unit RENAME TO interrogation;
ALTER TABLE interrogation RENAME COLUMN id TO survey_unit_id;
ALTER TABLE interrogation RENAME COLUMN interrogation_id TO id;


ALTER TABLE public.survey_unit_temp_zone RENAME TO interrogation_temp_zone;

ALTER TABLE public."comment" DROP CONSTRAINT "FKmp8mo44go4vhohovjaxxg8140";
ALTER TABLE public."data" DROP CONSTRAINT "FK7ym9pbkxwahn9vpf2fgoaxxuq";
ALTER TABLE public.paradata_event DROP CONSTRAINT "FK506gklsgdfiner7hb3vbo77ku";
ALTER TABLE public.personalization DROP CONSTRAINT "FK9aonche3cbcolkeuacv4v6hk";
ALTER TABLE public.state_data DROP CONSTRAINT "FKkjjh680qs400ap1dko1kmqh0s";
ALTER TABLE public.interrogation_temp_zone DROP CONSTRAINT "surveyUnitTempZoneId";
ALTER TABLE public.interrogation DROP CONSTRAINT "survey_unitPK";

ALTER TABLE public."comment" RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public."data" RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.paradata_event ADD COLUMN interrogation_id VARCHAR(255);
ALTER TABLE public.personalization RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.state_data RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.interrogation_temp_zone RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.interrogation_temp_zone RENAME COLUMN survey_unit TO interrogation;
ALTER TABLE public.interrogation_temp_zone REPLICA IDENTITY FULL;

update public."comment" set interrogation_id = i.id from interrogation i
  where i.survey_unit_id = interrogation_id;
update public."data" set interrogation_id = i.id from interrogation i
  where i.survey_unit_id = interrogation_id;
update public."paradata_event" set interrogation_id = i.id from interrogation i
  where i.survey_unit_id = "paradata_event".survey_unit_id;
update public."personalization" set interrogation_id = i.id from interrogation i
  where i.survey_unit_id = interrogation_id;
update public."state_data" set interrogation_id = i.id from interrogation i
  where i.survey_unit_id = interrogation_id;
update public."interrogation_temp_zone" set interrogation_id = i.id from interrogation i
  where i.survey_unit_id = interrogation_id;

ALTER TABLE interrogation ADD CONSTRAINT interrogation_pkey PRIMARY KEY (id);

ALTER TABLE public."comment"
  ADD CONSTRAINT "FK_comment_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id) ON DELETE CASCADE;

ALTER TABLE public."data"
  ADD CONSTRAINT "FK_data_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id) ON DELETE CASCADE;

ALTER TABLE public.personalization
  ADD CONSTRAINT "FK_personalization_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id) ON DELETE CASCADE;

ALTER TABLE public.state_data
  ADD CONSTRAINT "FK_state_data_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id) ON DELETE CASCADE;

ALTER TABLE public."interrogation_temp_zone"
  ADD CONSTRAINT "FK_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id) ON DELETE CASCADE;

DROP INDEX idx_personalization_su;
DROP INDEX idx_comment_su;
DROP INDEX idx_data_su;
DROP INDEX idx_state_data_su;

CREATE UNIQUE INDEX idx_personalization_interrogation
  ON personalization(interrogation_id);
CREATE UNIQUE INDEX idx_comment_interrogation
  ON comment(interrogation_id);
CREATE UNIQUE INDEX idx_data_interrogation
  ON data(interrogation_id);
CREATE UNIQUE INDEX idx_state_data_interrogation
  ON state_data(interrogation_id);

