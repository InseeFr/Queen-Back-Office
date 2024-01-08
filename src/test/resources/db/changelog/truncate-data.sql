--changeset davdarras:empty-data context:test

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

TRUNCATE TABLE public.survey_unit_temp_zone, public.state_data, public.paradata_event,
    public.data, public.comment, public.personalization, public.survey_unit,
    public.required_nomenclature, public.questionnaire_model, public.nomenclature,
    public.metadata, public.campaign;