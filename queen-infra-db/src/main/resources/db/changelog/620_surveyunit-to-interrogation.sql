--liquibase formatted sql

--changeset davdarras:620-0
ALTER TABLE public.survey_unit RENAME TO interrogation;
ALTER TABLE public.interrogation RENAME CONSTRAINT "survey_unitPK" TO interrogationPK;

ALTER TABLE public."comment" RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public."comment" DROP CONSTRAINT "FKmp8mo44go4vhohovjaxxg8140";
ALTER TABLE public."comment"
  ADD CONSTRAINT "FK_comment_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id);
ALTER INDEX idx_comment_su RENAME TO idx_comment_interrogation;

ALTER TABLE public."data" RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public."data" DROP CONSTRAINT "FK7ym9pbkxwahn9vpf2fgoaxxuq";
ALTER TABLE public."data"
  ADD CONSTRAINT "FK_data_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id);
ALTER INDEX idx_data_su RENAME TO idx_data_interrogation;

ALTER TABLE public.paradata_event RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.paradata_event DROP CONSTRAINT "FK506gklsgdfiner7hb3vbo77ku";
ALTER TABLE public.paradata_event
  ADD CONSTRAINT "FK_paradata_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id);

ALTER TABLE public.personalization RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.personalization DROP CONSTRAINT "FK9aonche3cbcolkeuacv4v6hk";
ALTER TABLE public.personalization
  ADD CONSTRAINT "FK_personalization_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id);
ALTER INDEX idx_personalization_su RENAME TO idx_personalization_interrogation;

ALTER TABLE public.state_data RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.state_data DROP CONSTRAINT "FKkjjh680qs400ap1dko1kmqh0s";
ALTER TABLE public.state_data
  ADD CONSTRAINT "FK_state_data_interrogation"
  FOREIGN KEY (interrogation_id) REFERENCES public.interrogation(id);
ALTER INDEX idx_state_data_su RENAME TO idx_state_data_interrogation;

ALTER TABLE public.survey_unit_temp_zone RENAME TO interrogation_temp_zone;
ALTER TABLE public.interrogation_temp_zone RENAME CONSTRAINT "surveyUnitTempZoneId" TO interrogationTempZonePK;
ALTER TABLE public.interrogation_temp_zone RENAME COLUMN survey_unit_id TO interrogation_id;
ALTER TABLE public.interrogation_temp_zone RENAME COLUMN survey_unit TO interrogation;
