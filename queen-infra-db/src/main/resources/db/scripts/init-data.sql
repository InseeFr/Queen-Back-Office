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

TRUNCATE TABLE public.survey_unit_temp_zone, public.state_data, public.paradata_event,
    public.data, public.comment, public.personalization, public.survey_unit,
    public.required_nomenclature, public.questionnaire_model, public.nomenclature,
    public.metadata, public.campaign;

INSERT INTO public.campaign VALUES ('SIMPSONS2020X00', 'Survey on the Simpsons tv show 2020');
INSERT INTO public.campaign VALUES ('VQS2021X00', 'Everyday life and health survey 2021');
INSERT INTO public.campaign VALUES ('LOG2021X11Web', 'Enquête Logement 2022 - Séquence 1 - HR - Web');
INSERT INTO public.campaign VALUES ('LOG2021X11Tel', 'Enquête Logement 2022 - Séquence 1 - HR');

--
-- TOC entry 3406 (class 0 OID 16489)
-- Dependencies: 219
-- Data for Name: metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.metadata VALUES ('6ce93fc8-1abd-4da3-b251-805943948954', '{}', 'SIMPSONS2020X00');
INSERT INTO public.metadata VALUES ('0fb58fa2-e26a-4a68-9ca7-6ec63bb2fb71', '{}', 'VQS2021X00');
INSERT INTO public.metadata VALUES ('09a6cf03-2998-4451-9cc0-522b7c7f423a', '{"variables": [{"name": "Enq_LibelleEnquete", "value": "Enquête logement pour la recette technique"}, {"name": "Enq_ObjectifsCourts", "value": "Cette enquête permet de connaître votre logement mais surtout nos applis"}, {"name": "Enq_CaractereObligatoire", "value": true}, {"name": "Enq_NumeroVisa", "value": "2021A054EC"}, {"name": "Enq_MinistereTutelle", "value": "de l''Économie, des Finances et de la Relance"}, {"name": "Enq_ParutionJo", "value": true}, {"name": "Enq_DateParutionJo", "value": "23/11/2020"}, {"name": "Enq_RespOperationnel", "value": "L’Institut national de la statistique et des études économiques (Insee)"}, {"name": "Enq_RespTraitement", "value": "l''Insee"}, {"name": "Enq_AnneeVisa", "value": "2021"}, {"name": "Loi_statistique", "value": "https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000888573"}, {"name": "Loi_rgpd", "value": "https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=CELEX%3A32016R0679"}, {"name": "Loi_informatique", "value": "https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000886460"}], "inseeContext": "household"}', 'LOG2021X11Web');
INSERT INTO public.metadata VALUES ('186d53db-a653-44f2-abda-f0f1d1cddfe2', '{}', 'LOG2021X11Tel');

--
-- TOC entry 3401 (class 0 OID 16411)
-- Dependencies: 214
-- Data for Name: nomenclature; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.nomenclature VALUES ('L_PAYSNAIS', 'pays', '[{"id": "001", "label": "FRANCE", "codenum": "250", "codealpha": "FRA", "libelle_cog": "FRANCE"}, {"id": "002", "label": "BELGIQUE", "codenum": "056", "codealpha": "BEL", "libelle_cog": "BELGIQUE"}]');
INSERT INTO public.nomenclature VALUES ('L_DEPNAIS', 'départements français', '[{"id": "01", "label": "AIN (01)"}, {"id": "02", "label": "AISNE (02)"}]');
INSERT INTO public.nomenclature VALUES ('cog-communes', 'communes françaises', '[{"id": "10002", "label": "Ailleville", "nccenr": "Ailleville"}, {"id": "10003", "label": "Aix-en-Othe", "nccenr": "Aix-en-Othe"}]');
INSERT INTO public.nomenclature VALUES ('cities2019', 'french cities 2019', '[{"arr": "012", "can": "0108", "com": "01001", "dep": "01", "ncc": "ABERGEMENT CLEMENCIAT", "reg": 84, "tncc": 5, "nccenr": "Abergement-Clémenciat", "libelle": "L''Abergement-Clémenciat", "typecom": "COM", "comparent": ""}, {"arr": "011", "can": "0101", "com": "01002", "dep": "01", "ncc": "ABERGEMENT DE VAREY", "reg": 84, "tncc": 5, "nccenr": "Abergement-de-Varey", "libelle": "L''Abergement-de-Varey", "typecom": "COM", "comparent": ""}]');
INSERT INTO public.nomenclature VALUES ('regions2019', 'french regions 2019', '[{"ncc": "GUADELOUPE", "reg": "01", "tncc": 3, "nccenr": "Guadeloupe", "libelle": "Guadeloupe", "cheflieu": 97105}, {"ncc": "MARTINIQUE", "reg": "02", "tncc": 3, "nccenr": "Martinique", "libelle": "Martinique", "cheflieu": 97209}]');
INSERT INTO public.nomenclature VALUES ('L_NATIONETR', 'nationalités', '[{"id": "001", "label": "FRANCAISE", "codenum": "250", "libelle": "FRANCAISE", "codealpha": "FRA"}, {"id": "002", "label": "BELGE", "codenum": "056", "libelle": "BELGE", "codealpha": "BEL"}, {"id": "003", "label": "NEERLANDAISE, HOLLANDAISE", "codenum": "528", "libelle": "NEERLANDAISE", "codealpha": "NLD"}, {"id": "004", "label": "ALLEMANDE", "codenum": "276", "libelle": "ALLEMANDE", "codealpha": "DEU"}]');

--
-- TOC entry 3403 (class 0 OID 16419)
-- Dependencies: 216
-- Data for Name: questionnaire_model; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.questionnaire_model VALUES ('QmWithoutCamp', 'Questionnaire with no campaign', '{"id": "i6vwi2506qf2mms", "label": "Questionnaire SIMPSONS 20201215", "modele": "SIMPSONS", "maxPage": "37", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.2.11", "generatingDate": "16-09-2021 09:08:11", "lunaticModelVersion": "2.2.3"}', NULL);
INSERT INTO public.questionnaire_model VALUES ('simpsons', 'Questionnaire about the Simpsons tv show', '{"id": "i6vwi2506qf2mms", "label": "Questionnaire SIMPSONS 20201215", "modele": "SIMPSONS", "maxPage": "37", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.2.11", "generatingDate": "16-09-2021 09:08:11", "lunaticModelVersion": "2.2.3"}', 'SIMPSONS2020X00');
INSERT INTO public.questionnaire_model VALUES ('simpsonsV2', 'Questionnaire about the Simpsons tv show version 2', '{"id": "i6vwi2506qf2mms", "label": "Questionnaire SIMPSONS 20201215", "modele": "SIMPSONS", "maxPage": "37", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.2.11", "generatingDate": "16-09-2021 09:08:11", "lunaticModelVersion": "2.2.3"}', 'SIMPSONS2020X00');
INSERT INTO public.questionnaire_model VALUES ('VQS2021X00', 'Questionnaire of the Everyday life and health survey 2021', '{"id": "k1g74VQS2bisqf2", "label": "Questionnaire simple rallye game", "modele": "VQS2021", "maxPage": "21", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.3.7", "generatingDate": "21-04-2022 13:17:27", "lunaticModelVersion": "2.2.10"}', 'VQS2021X00');
INSERT INTO public.questionnaire_model VALUES ('LOG2021X11Web', 'Enquête Logement 2022 - Séquence 1 - HR - Web', '{"id": "kwdqpj7a", "label": "Enquête Logement - Partie 1", "modele": "m1", "maxPage": "184", "missing": false, "variables": []}', 'LOG2021X11Web');
INSERT INTO public.questionnaire_model VALUES ('LOG2021X11Tel', 'Enquête Logement 2022 - Séquence 1 - HR', '{"id": "kwdqpj7a", "label": "Enquête Logement - Partie 1"}', 'LOG2021X11Tel');

--
-- TOC entry 3405 (class 0 OID 16427)
-- Dependencies: 218
-- Data for Name: required_nomenclature; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.required_nomenclature VALUES ('QmWithoutCamp', 'cities2019');
INSERT INTO public.required_nomenclature VALUES ('simpsons', 'cities2019');
INSERT INTO public.required_nomenclature VALUES ('simpsons', 'regions2019');
INSERT INTO public.required_nomenclature VALUES ('simpsonsV2', 'regions2019');
INSERT INTO public.required_nomenclature VALUES ('VQS2021X00', 'cities2019');
INSERT INTO public.required_nomenclature VALUES ('VQS2021X00', 'regions2019');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Web', 'L_PAYSNAIS');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Web', 'cog-communes');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Web', 'L_DEPNAIS');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Web', 'L_NATIONETR');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Tel', 'L_PAYSNAIS');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Tel', 'cog-communes');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Tel', 'L_DEPNAIS');
INSERT INTO public.required_nomenclature VALUES ('LOG2021X11Tel', 'L_NATIONETR');

--
-- TOC entry 3404 (class 0 OID 16424)
-- Dependencies: 217
-- Data for Name: survey_unit; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.survey_unit VALUES ('11', 'SIMPSONS2020X00', 'simpsons');
INSERT INTO public.survey_unit VALUES ('12', 'SIMPSONS2020X00', 'simpsons');
INSERT INTO public.survey_unit VALUES ('13', 'SIMPSONS2020X00', 'simpsonsV2');
INSERT INTO public.survey_unit VALUES ('14', 'SIMPSONS2020X00', 'simpsonsV2');
INSERT INTO public.survey_unit VALUES ('20', 'VQS2021X00', 'VQS2021X00');
INSERT INTO public.survey_unit VALUES ('21', 'VQS2021X00', 'VQS2021X00');
INSERT INTO public.survey_unit VALUES ('22', 'VQS2021X00', 'VQS2021X00');
INSERT INTO public.survey_unit VALUES ('23', 'VQS2021X00', 'VQS2021X00');
INSERT INTO public.survey_unit VALUES ('LOG2021X11Web-01', 'LOG2021X11Web', 'LOG2021X11Web');
INSERT INTO public.survey_unit VALUES ('LOG2021X11Web-02', 'LOG2021X11Web', 'LOG2021X11Web');
INSERT INTO public.survey_unit VALUES ('LOG2021X11Web-03', 'LOG2021X11Web', 'LOG2021X11Web');
INSERT INTO public.survey_unit VALUES ('LOG2021X11Tel_01', 'LOG2021X11Tel', 'LOG2021X11Tel');
INSERT INTO public.survey_unit VALUES ('LOG2021X11Tel_02', 'LOG2021X11Tel', 'LOG2021X11Tel');
INSERT INTO public.survey_unit VALUES ('LOG2021X11Tel_03', 'LOG2021X11Tel', 'LOG2021X11Tel');

--
-- TOC entry 3399 (class 0 OID 16396)
-- Dependencies: 212
-- Data for Name: comment; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.comment VALUES ('a78366f8-8653-448a-8754-53a3135a2137', '{"COMMENT": "un commentaire"}', '11');
INSERT INTO public.comment VALUES ('7d87278a-c6fc-4989-829c-9867864ef74b', '{}', '12');
INSERT INTO public.comment VALUES ('67945983-430a-4b34-99e4-86ee75e0e27a', '{}', '13');
INSERT INTO public.comment VALUES ('56bfe7ef-1ea6-49a4-bd37-d46ddee36fcc', '{}', '14');
INSERT INTO public.comment VALUES ('ee175940-8f81-4d52-8e72-1734e8f3f0fe', '{}', '20');
INSERT INTO public.comment VALUES ('86f7a230-1408-4569-b663-9817840ab7da', '{}', '21');
INSERT INTO public.comment VALUES ('c0bc2f92-be6b-4ebd-b353-a04d05f6d314', '{}', '22');
INSERT INTO public.comment VALUES ('692a1749-e293-4bcf-8456-4fb8edc9a5a7', '{}', '23');
INSERT INTO public.comment VALUES ('53912a39-0e87-4f3c-804b-32910a2c1e6e', '{}', 'LOG2021X11Web-01');
INSERT INTO public.comment VALUES ('c42ebc95-f35c-4a34-be61-5b1e9c1a37fe', '{}', 'LOG2021X11Web-02');
INSERT INTO public.comment VALUES ('d8b683e0-850e-487f-bc8d-6f3c9440e32b', '{}', 'LOG2021X11Web-03');
INSERT INTO public.comment VALUES ('d6f92b88-0b80-41dc-a1b3-e69b2fb71846', '{}', 'LOG2021X11Tel_01');
INSERT INTO public.comment VALUES ('70ee3af3-fd2c-4745-b0bb-73124fa016b8', '{}', 'LOG2021X11Tel_02');
INSERT INTO public.comment VALUES ('833b5a5d-845e-4b3e-a725-d444907ee476', '{}', 'LOG2021X11Tel_03');


--
-- TOC entry 3400 (class 0 OID 16403)
-- Dependencies: 213
-- Data for Name: data; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.data VALUES ('6cb4378d-aa70-4add-bb61-1f2fdc86dfbb', '{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}, "COLLECTED": {"READY": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": true}, "COMMENT": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Love it !"}, "PRODUCER": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Matt Groening"}}}', '11');
INSERT INTO public.data VALUES ('cf72a231-b40f-4ffa-9834-bf4e40bf85ac', '{}', '12');
INSERT INTO public.data VALUES ('f63bbfbe-9926-48ae-8d04-421296a40634', '{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}, "COLLECTED": {"READY": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": true}, "COMMENT": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Love it !"}, "PRODUCER": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Matt Groening"}}}', '13');
INSERT INTO public.data VALUES ('8e3b28cc-74b1-4391-8359-c495538129b7', '{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}, "COLLECTED": {"READY": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": true}, "COMMENT": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Love it !"}, "PRODUCER": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Matt Groening"}}}', '14');
INSERT INTO public.data VALUES ('e9e97450-ef9c-4f49-9375-adf11b6a158b', '{}', '20');
INSERT INTO public.data VALUES ('42dc1400-0a36-4c20-8742-115e22c42369', '{}', '21');
INSERT INTO public.data VALUES ('4540afba-ee51-42e4-bf74-d2346d813e89', '{}', '22');
INSERT INTO public.data VALUES ('757170c2-b2d5-4c71-85c1-61988b36e416', '{}', '23');
INSERT INTO public.data VALUES ('27abfaed-187a-44ab-8287-af08f3bd7158', '{}', 'LOG2021X11Web-01');
INSERT INTO public.data VALUES ('c118114a-c0be-462d-9fe9-604436bea20a', '{}', 'LOG2021X11Web-02');
INSERT INTO public.data VALUES ('df044ba3-9abb-451e-9e4d-75ba98ace5e6', '{}', 'LOG2021X11Web-03');
INSERT INTO public.data VALUES ('fdc43238-cf8e-4a55-ad49-14ea8152728d', '{}', 'LOG2021X11Tel_01');
INSERT INTO public.data VALUES ('fa0a7a90-0324-429f-837b-ad06b79cfd7d', '{}', 'LOG2021X11Tel_02');
INSERT INTO public.data VALUES ('d51e29b9-b27b-4159-957c-6bb54d811a20', '{}', 'LOG2021X11Tel_03');

--
-- TOC entry 3407 (class 0 OID 16494)
-- Dependencies: 220
-- Data for Name: paradata_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.paradata_event VALUES ('ff45a68e-4f76-4875-b4f7-80730d3a6e35', '{"idSU": "20"}', '20');
INSERT INTO public.paradata_event VALUES ('cd0917a0-2239-425f-9395-5a87f875b060', '{"idSU": "20"}', '20');
INSERT INTO public.paradata_event VALUES ('e01d5d4b-fb01-403b-b4b3-d3841e0d12c9', '{"idSU": "21"}', '21');
INSERT INTO public.paradata_event VALUES ('84c5e7c3-1ce5-4f9a-a192-141e9f81cd9e', '{"idSU": "21"}', '21');
INSERT INTO public.paradata_event VALUES ('1993a133-bcd5-4c93-9eb4-12040d33fa09', '{"idSU": "22"}', '22');
INSERT INTO public.paradata_event VALUES ('c9f65d68-a64c-4ab9-9a33-69ce24df5920', '{"idSU": "22"}', '22');
INSERT INTO public.paradata_event VALUES ('408b3991-8c76-4712-aafb-c493388fb3ea', '{"idSU": "23"}', '23');
INSERT INTO public.paradata_event VALUES ('75a27d77-009d-49eb-a3e2-044ec245b2fc', '{"idSU": "23"}', '23');
INSERT INTO public.paradata_event VALUES ('6a422a3c-e255-4b88-b553-03a688425e43', '{"idSU": "LOG2021X11Web-01"}', 'LOG2021X11Web-01');
INSERT INTO public.paradata_event VALUES ('e9be3275-c9f7-4d56-a480-e95275ba80ca', '{"idSU": "LOG2021X11Web-01"}', 'LOG2021X11Web-01');
INSERT INTO public.paradata_event VALUES ('ae2df2f2-8b3d-4e94-83cf-053ed30b32b6', '{"idSU": "LOG2021X11Web-02"}', 'LOG2021X11Web-02');
INSERT INTO public.paradata_event VALUES ('54a86bc0-f291-4d9d-9e53-bae006a6ea16', '{"idSU": "LOG2021X11Web-02"}', 'LOG2021X11Web-02');
INSERT INTO public.paradata_event VALUES ('eb2b308e-bb5f-4f62-84cc-c44c4a9269de', '{"idSU": "LOG2021X11Web-03"}', 'LOG2021X11Web-03');
INSERT INTO public.paradata_event VALUES ('a112b2f2-4724-4cbd-9fe4-bac049411cde', '{"idSU": "LOG2021X11Web-03"}', 'LOG2021X11Web-03');
INSERT INTO public.paradata_event VALUES ('d2793581-22ab-4772-acc5-8c151cd0ec72', '{"idSU": "LOG2021X11Tel_01"}', 'LOG2021X11Tel_01');
INSERT INTO public.paradata_event VALUES ('4608f5a2-3f1e-4231-a266-87001680aef4', '{"idSU": "LOG2021X11Tel_01"}', 'LOG2021X11Tel_01');
INSERT INTO public.paradata_event VALUES ('c348372a-14f1-4950-8909-8bdf712acb11', '{"idSU": "LOG2021X11Tel_02"}', 'LOG2021X11Tel_02');
INSERT INTO public.paradata_event VALUES ('0d153c9f-e117-42e1-9b6b-3424c41c45ce', '{"idSU": "LOG2021X11Tel_02"}', 'LOG2021X11Tel_02');
INSERT INTO public.paradata_event VALUES ('9d38f37e-05bd-42f9-a33b-c459c257ffc5', '{"idSU": "LOG2021X11Tel_03"}', 'LOG2021X11Tel_03');
INSERT INTO public.paradata_event VALUES ('0ad9817e-5952-4d83-b219-717fbbae860d', '{"idSU": "LOG2021X11Tel_03"}', 'LOG2021X11Tel_03');


--
-- TOC entry 3408 (class 0 OID 16499)
-- Dependencies: 221
-- Data for Name: personalization; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.personalization VALUES ('22fda6de-c0c0-4b3c-b10c-2a2b705644c7', '[{"name": "whoAnswers1", "value": "Mr Dupond"}, {"name": "whoAnswers2", "value": ""}]', '11');
INSERT INTO public.personalization VALUES ('86ee92ff-0d5d-4034-8cfb-a29f81384918', '[]', '12');
INSERT INTO public.personalization VALUES ('6fcbbd84-3464-4290-b8fc-cdf0082ee339', '[]', '13');
INSERT INTO public.personalization VALUES ('f683f639-1da5-4219-95a2-2df4df0a2a0d', '[]', '14');
INSERT INTO public.personalization VALUES ('65ec0765-4a5e-4518-be79-bc2be6b882b8', '[]', '20');
INSERT INTO public.personalization VALUES ('fc73766c-ffb7-4443-9135-1e39939320a0', '[]', '21');
INSERT INTO public.personalization VALUES ('d14499b0-93f2-4722-a624-47d7cafc26a6', '[]', '22');
INSERT INTO public.personalization VALUES ('a7e878fa-d12a-4a25-bc17-08a583b0127d', '[]', '23');
INSERT INTO public.personalization VALUES ('eec3ae3f-ad9e-45d6-b2f8-191e19f2a571', '[]', 'LOG2021X11Web-01');
INSERT INTO public.personalization VALUES ('3cf06171-2f86-4724-8c86-fb9b2f40286a', '[]', 'LOG2021X11Web-02');
INSERT INTO public.personalization VALUES ('1683a6ff-3c40-47c8-8862-9c685c6f5e88', '[]', 'LOG2021X11Web-03');
INSERT INTO public.personalization VALUES ('7c4b7cfb-7b22-43d1-84f3-07eb808a33ce', '[]', 'LOG2021X11Tel_01');
INSERT INTO public.personalization VALUES ('9910a916-d270-47f7-9c30-19c456323831', '[]', 'LOG2021X11Tel_02');
INSERT INTO public.personalization VALUES ('8b377092-33da-4494-a5fc-d2825d755e2c', '[]', 'LOG2021X11Tel_03');

--
-- TOC entry 3409 (class 0 OID 16504)
-- Dependencies: 222
-- Data for Name: state_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.state_data VALUES ('f55c7388-0724-4b3e-9c78-536ee2dee5f6', '2.3#5', 1111111111, 'EXTRACTED', '11');
INSERT INTO public.state_data VALUES ('c11f8aae-5201-4a16-89d8-5f8b4c6ab942', '2.3#5', 1111111111, 'INIT', '12');
INSERT INTO public.state_data VALUES ('1fe17624-70d0-48e2-ba50-041cc23cbeeb', '2.3#5', 1111111111, 'INIT', '13');
INSERT INTO public.state_data VALUES ('164cc2b6-b58f-4011-a064-01f5f761326b', '2.3#5', 1111111111, 'INIT', '14');
INSERT INTO public.state_data VALUES ('42858b14-2a0c-4d17-afd0-f50a0f9a8dd5', '1', 900000000, 'INIT', '20');
INSERT INTO public.state_data VALUES ('a2067072-8887-4ba0-8493-d91f50734d95', '1', 900000000, 'INIT', '21');
INSERT INTO public.state_data VALUES ('1b7d2c56-aa0b-4d2a-8193-2aec2484f5ee', '1', 900000000, 'INIT', '22');
INSERT INTO public.state_data VALUES ('5cb8424f-6300-4236-86f4-ffcc90ebb6b4', '1', 900000000, 'INIT', '23');
INSERT INTO public.state_data VALUES ('bfe007af-29c9-4a12-a3b7-745d0a19c5f5', '1', 900000000, 'INIT', 'LOG2021X11Web-01');
INSERT INTO public.state_data VALUES ('d17cd723-5674-4754-80bd-05a36235abb6', '1', 900000000, 'INIT', 'LOG2021X11Web-02');
INSERT INTO public.state_data VALUES ('f024ca52-95e5-4d4d-a15f-6111c68ff83e', '1', 900000000, 'INIT', 'LOG2021X11Web-03');
INSERT INTO public.state_data VALUES ('ffa9847f-c2fa-4b50-a017-afcbf6a9c205', '1', 900000000, 'INIT', 'LOG2021X11Tel_01');
INSERT INTO public.state_data VALUES ('9affeeb6-84f8-4f8c-bc7d-31b27202c8ab', '1', 900000000, 'INIT', 'LOG2021X11Tel_02');
INSERT INTO public.state_data VALUES ('e75e53d5-66a4-4ab9-922a-a84e5709e8c9', '1', 900000000, 'INIT', 'LOG2021X11Tel_03');

--
-- TOC entry 3410 (class 0 OID 16540)
-- Dependencies: 223
-- Data for Name: survey_unit_temp_zone; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.survey_unit_temp_zone VALUES ('42858b14-2a0c-4d17-afd0-f50a0f9a8dd5','temp-11', 'user-id', 900000000, '{"data": {"EXTERNAL": {"ADR": "Rue des Plantes","NUMTH": "1"}},"comment": {},"personalization": [],"questionnaireId": "questionnaire-11"}');
INSERT INTO public.survey_unit_temp_zone VALUES ('6fcbbd84-3464-4290-b8fc-cdf0082ee339','temp-12', 'user-id', 900000000, '{"data": {"EXTERNAL": {"ADR": "Rue des Plantes","NUMTH": "1"}},"comment": {},"personalization": [],"questionnaireId": "questionnaire-12"}');
