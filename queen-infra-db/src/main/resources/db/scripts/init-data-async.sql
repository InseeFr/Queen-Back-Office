--changeset davdarras:test-data context:test

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

--
-- TOC entry 3402 (class 0 OID 16416)
-- Dependencies: 215
-- Data for Name: campaign; Type: TABLE DATA; Schema: public; Owner: postgres
--

TRUNCATE TABLE public.interrogation_temp_zone, public.state_data, public.paradata_event,
    public.data, public.comment, public.personalization, public.interrogation,
    public.required_nomenclature, public.questionnaire_model, public.nomenclature,
    public.metadata, public.campaign;

INSERT INTO public.campaign VALUES ('GEN2025A00', 'Asynchrone test');

--
-- TOC entry 3406 (class 0 OID 16489)
-- Dependencies: 219
-- Data for Name: metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.metadata VALUES ('6ce93fc8-1abd-4da3-b251-805943948954', '{}', 'GEN2025A00');

--
-- TOC entry 3403 (class 0 OID 16419)
-- Dependencies: 216
-- Data for Name: questionnaire_model; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.questionnaire_model VALUES ('quest_model_entreprise_generique_2025', 'Questionnaire about the Simpsons tv show', '{"id": "i6vwi2506qf2mms", "label": "Questionnaire SIMPSONS 20201215", "modele": "SIMPSONS", "maxPage": "37", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.2.11", "generatingDate": "16-09-2021 09:08:11", "lunaticModelVersion": "2.2.3"}', 'GEN2025A00');

