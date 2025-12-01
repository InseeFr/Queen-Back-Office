--changeset davdarras:test-data context:test

--
-- TOC entry 3402 (class 0 OID 16416)
-- Dependencies: 215
-- Data for Name: campaign; Type: TABLE DATA; Schema: public; Owner: postgres
--

TRUNCATE TABLE public.interrogation_temp_zone, public.state_data, public.paradata_event,
    public.data, public.comment, public.personalization, public.interrogation,
    public.required_nomenclature, public.questionnaire_model, public.nomenclature,
    public.metadata, public.campaign;

INSERT INTO public.campaign(id, label, sensitivity) VALUES
  ('SIMPSONS2020X00', 'Survey on the Simpsons tv show 2020', 'NORMAL'),
  ('VQS2021X00', 'Everyday life and health survey 2021', 'NORMAL'),
  ('LOG2021X11Web', 'Enquête Logement 2022 - Séquence 1 - HR - Web', 'SENSITIVE'),
  ('LOG2021X11Tel', 'Enquête Logement 2022 - Séquence 1 - HR', 'NORMAL');

--
-- TOC entry 3406 (class 0 OID 16489)
-- Dependencies: 219
-- Data for Name: metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.metadata(id, value, campaign_id) VALUES 
  ('6ce93fc8-1abd-4da3-b251-805943948954', '{}', 'SIMPSONS2020X00'),
  ('0fb58fa2-e26a-4a68-9ca7-6ec63bb2fb71', '{}', 'VQS2021X00'),
  ('09a6cf03-2998-4451-9cc0-522b7c7f423a', '{  "logos": [{"url": "https://insee.fr/logo1.png","label": "logo1"},{"url":"https://insee.fr/logo2.png","label":"logo2"},{"url":"https://insee.fr/logo3.png","label":"logo3"}],"variables": [{"name": "Enq_LibelleEnquete", "value": "Enquête logement pour la recette technique"}, {"name": "Enq_ObjectifsCourts", "value": "Cette enquête permet de connaître votre logement mais surtout nos applis"}, {"name": "Enq_CaractereObligatoire", "value": true}, {"name": "Enq_NumeroVisa", "value": "2021A054EC"}, {"name": "Enq_MinistereTutelle", "value": "de l''Économie, des Finances et de la Relance"}, {"name": "Enq_ParutionJo", "value": true}, {"name": "Enq_DateParutionJo", "value": "23/11/2020"}, {"name": "Enq_RespOperationnel", "value": "L’Institut national de la statistique et des études économiques (Insee)"}, {"name": "Enq_RespTraitement", "value": "l''Insee"}, {"name": "Enq_AnneeVisa", "value": "2021"}, {"name": "Loi_statistique", "value": "https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000888573"}, {"name": "Loi_rgpd", "value": "https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=CELEX%3A32016R0679"}, {"name": "Loi_informatique", "value": "https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000886460"}], "inseeContext": "household"}', 'LOG2021X11Web'),
  ('186d53db-a653-44f2-abda-f0f1d1cddfe2', '{}', 'LOG2021X11Tel');

--
-- TOC entry 3401 (class 0 OID 16411)
-- Dependencies: 214
-- Data for Name: nomenclature; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.nomenclature(id, label, value) VALUES
  ('L_PAYSNAIS', 'pays', '[{"id": "001", "label": "FRANCE", "codenum": "250", "codealpha": "FRA", "libelle_cog": "FRANCE"}, {"id": "002", "label": "BELGIQUE", "codenum": "056", "codealpha": "BEL", "libelle_cog": "BELGIQUE"}]'),
  ('L_DEPNAIS', 'départements français', '[{"id": "01", "label": "AIN (01)"}, {"id": "02", "label": "AISNE (02)"}]'),
  ('cog-communes', 'communes françaises', '[{"id": "10002", "label": "Ailleville", "nccenr": "Ailleville"}, {"id": "10003", "label": "Aix-en-Othe", "nccenr": "Aix-en-Othe"}]'),
  ('cities2019', 'french cities 2019', '[{"id": "012", "can": "0108", "com": "01001", "dep": "01", "ncc": "ABERGEMENT CLEMENCIAT", "reg": 84, "tncc": "5", "nccenr": "Abergement-Clémenciat", "label": "L''Abergement-Clémenciat", "typecom": "COM", "comparent": ""}, {"id": "011", "can": "0101", "com": "01002", "dep": "01", "ncc": "ABERGEMENT DE VAREY", "reg": 84, "tncc": "5", "nccenr": "Abergement-de-Varey", "label": "L''Abergement-de-Varey", "typecom": "COM", "comparent": ""}]'),
  ('regions2019', 'french regions 2019', '[{"id": "GUADELOUPE", "reg": "01", "tncc": "3", "nccenr": "Guadeloupe", "label": "Guadeloupe", "cheflieu": "97105"}, {"id": "MARTINIQUE", "reg": "02", "tncc": "3", "nccenr": "Martinique", "label": "Martinique", "cheflieu": "97209"}]'),
  ('L_NATIONETR', 'nationalités', '[{"id": "001", "label": "FRANCAISE", "codenum": "250", "label": "FRANCAISE", "codealpha": "FRA"}, {"id": "002", "label": "BELGE", "codenum": "056", "label": "BELGE", "codealpha": "BEL"}, {"id": "003", "label": "NEERLANDAISE, HOLLANDAISE", "codenum": "528", "label": "NEERLANDAISE", "codealpha": "NLD"}, {"id": "004", "label": "ALLEMANDE", "codenum": "276", "label": "ALLEMANDE", "codealpha": "DEU"}]');

--
-- TOC entry 3403 (class 0 OID 16419)
-- Dependencies: 216
-- Data for Name: questionnaire_model; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES
  ('QmWithoutCamp', 'Questionnaire with no campaign', '{"id": "i6vwi2506qf2mms", "label": "Questionnaire SIMPSONS 20201215", "modele": "SIMPSONS", "maxPage": "37", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.2.11", "generatingDate": "16-09-2021 09:08:11", "lunaticModelVersion": "2.2.3"}', NULL),
  ('simpsons', 'Questionnaire about the Simpsons tv show', '{"id": "i6vwi2506qf2mms", "label": "Questionnaire SIMPSONS 20201215", "modele": "SIMPSONS", "maxPage": "37", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.2.11", "generatingDate": "16-09-2021 09:08:11", "lunaticModelVersion": "2.2.3"}', 'SIMPSONS2020X00'),
  ('simpsonsV2', 'Questionnaire about the Simpsons tv show version 2', '{"id": "i6vwi2506qf2mms", "label": "Questionnaire SIMPSONS 20201215", "modele": "SIMPSONS", "maxPage": "37", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.2.11", "generatingDate": "16-09-2021 09:08:11", "lunaticModelVersion": "2.2.3"}', 'SIMPSONS2020X00'),
  ('VQS2021X00', 'Questionnaire of the Everyday life and health survey 2021', '{"id": "k1g74VQS2bisqf2", "label": "Questionnaire simple rallye game", "modele": "VQS2021", "maxPage": "21", "missing": true, "variables": [], "pagination": "question", "enoCoreVersion": "2.3.7", "generatingDate": "21-04-2022 13:17:27", "lunaticModelVersion": "2.2.10"}', 'VQS2021X00'),
  ('LOG2021X11Web', 'Enquête Logement 2022 - Séquence 1 - HR - Web', '{"id": "kwdqpj7a", "label": "Enquête Logement - Partie 1", "modele": "m1", "maxPage": "184", "missing": false, "variables": []}', 'LOG2021X11Web'),
  ('LOG2021X11Tel', 'Enquête Logement 2022 - Séquence 1 - HR', '{"id": "kwdqpj7a", "label": "Enquête Logement - Partie 1"}', 'LOG2021X11Tel');

--
-- TOC entry 3405 (class 0 OID 16427)
-- Dependencies: 218
-- Data for Name: required_nomenclature; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.required_nomenclature(id_required_nomenclature, code) VALUES
  ('QmWithoutCamp', 'cities2019'),
  ('simpsons', 'cities2019'),
  ('simpsons', 'regions2019'),
  ('simpsonsV2', 'regions2019'),
  ('VQS2021X00', 'cities2019'),
  ('VQS2021X00', 'regions2019'),
  ('LOG2021X11Web', 'L_PAYSNAIS'),
  ('LOG2021X11Web', 'cog-communes'),
  ('LOG2021X11Web', 'L_DEPNAIS'),
  ('LOG2021X11Web', 'L_NATIONETR'),
  ('LOG2021X11Tel', 'L_PAYSNAIS'),
  ('LOG2021X11Tel', 'cog-communes'),
  ('LOG2021X11Tel', 'L_DEPNAIS'),
  ('LOG2021X11Tel', 'L_NATIONETR');

--
-- TOC entry 3404 (class 0 OID 16424)
-- Dependencies: 217
-- Data for Name: interrogation; Type: TABLE DATA; Schema: public; Owner: postgres
--

-- Inserts adaptés avec UUIDv7 explicites pour la table interrogation et mapping sur les autres tables

INSERT INTO public.interrogation
(id, survey_unit_id, campaign_id, questionnaire_model_id, correlation_id)
VALUES
    ('517046b6-bd88-47e0-838e-00d03461f592', 'survey-unit-11',  'SIMPSONS2020X00', 'simpsons',    null),
    ('d98d28c2-1535-4fc8-a405-d6a554231bbc', 'survey-unit-12',  'SIMPSONS2020X00', 'simpsons',    null),
    ('c8142dcc-c133-49aa-a969-bb9828190a2c', 'survey-unit-13',  'SIMPSONS2020X00', 'simpsonsV2',  null),
    ('45c78a3e-f3b6-4d69-bd58-d2ca749dd7cd', 'survey-unit-14',  'SIMPSONS2020X00', 'simpsonsV2',  null),
    ('89f4df89-8e83-444f-8f43-6d964c3339d8', 'survey-unit-20',  'VQS2021X00',      'VQS2021X00',  null),
    ('ae9c355c-22d3-4188-bb7e-53b7638b988b', 'survey-unit-21',  'VQS2021X00',      'VQS2021X00',  null),
    ('77ae37f6-4e51-4282-adf2-e01b15e1ef09', 'survey-unit-22',  'VQS2021X00',      'VQS2021X00',  null),
    ('b1771f46-6916-4234-94bc-6a4fcd626637', 'survey-unit-23',  'VQS2021X00',      'VQS2021X00',  null),
    ('538d89c2-1047-48f7-8c16-02e9f41a8093', 'survey-unit-log-01', 'LOG2021X11Web','LOG2021X11Web', null),
    ('d128f9bf-e933-48bd-a7e5-8892a6a92997', 'survey-unit-log-02', 'LOG2021X11Web','LOG2021X11Web', null),
    ('d542db08-6d58-4665-9302-56eddf8185d2', 'survey-unit-log-03', 'LOG2021X11Web','LOG2021X11Web', null),
    ('89512ef9-4f39-466b-9388-a3626167f0c3', 'survey-unit-log-01', 'LOG2021X11Tel','LOG2021X11Tel', null),
    ('ec311ca8-41ec-4cbb-86ae-99745ffd93cb', 'survey-unit-log-02', 'LOG2021X11Tel','LOG2021X11Tel', null),
    ('31e0537d-01ab-4402-9a59-d001d3ba00fd', 'survey-unit-log-03', 'LOG2021X11Tel','LOG2021X11Tel', null),
    ('80dc2493-5258-44c5-8ec1-9c600d1df80b', 'survey-unit-diff-data', 'LOG2021X11Tel','LOG2021X11Tel', null),
    ('4f612c5d-8b60-46bd-a2de-1f0d861264db', 'survey-unit-diff-data', 'LOG2021X11Tel','LOG2021X11Tel', null);


INSERT INTO public.comment (id, value, interrogation_id) VALUES
  ('a78366f8-8653-448a-8754-53a3135a2137', '{"COMMENT": "un commentaire"}', '517046b6-bd88-47e0-838e-00d03461f592'),
  ('7d87278a-c6fc-4989-829c-9867864ef74b', '{}', 'd98d28c2-1535-4fc8-a405-d6a554231bbc'),
  ('67945983-430a-4b34-99e4-86ee75e0e27a', '{}', 'c8142dcc-c133-49aa-a969-bb9828190a2c'),
  ('56bfe7ef-1ea6-49a4-bd37-d46ddee36fcc', '{}', '45c78a3e-f3b6-4d69-bd58-d2ca749dd7cd'),
  ('ee175940-8f81-4d52-8e72-1734e8f3f0fe', '{}', '89f4df89-8e83-444f-8f43-6d964c3339d8'),
  ('86f7a230-1408-4569-b663-9817840ab7da', '{}', 'ae9c355c-22d3-4188-bb7e-53b7638b988b'),
  ('c0bc2f92-be6b-4ebd-b353-a04d05f6d314', '{}', '77ae37f6-4e51-4282-adf2-e01b15e1ef09'),
  ('692a1749-e293-4bcf-8456-4fb8edc9a5a7', '{}', 'b1771f46-6916-4234-94bc-6a4fcd626637'),
  ('53912a39-0e87-4f3c-804b-32910a2c1e6e', '{}', '538d89c2-1047-48f7-8c16-02e9f41a8093'),
  ('c42ebc95-f35c-4a34-be61-5b1e9c1a37fe', '{}', 'd128f9bf-e933-48bd-a7e5-8892a6a92997'),
  ('d8b683e0-850e-487f-bc8d-6f3c9440e32b', '{}', 'd542db08-6d58-4665-9302-56eddf8185d2'),
  ('d6f92b88-0b80-41dc-a1b3-e69b2fb71846', '{}', '89512ef9-4f39-466b-9388-a3626167f0c3'),
  ('70ee3af3-fd2c-4745-b0bb-73124fa016b8', '{}', 'ec311ca8-41ec-4cbb-86ae-99745ffd93cb'),
  ('833b5a5d-845e-4b3e-a725-d444907ee476', '{}', '31e0537d-01ab-4402-9a59-d001d3ba00fd'),
  ('692a1749-e293-4bcf-8456-4fb8edc9a5a8', '{}', '80dc2493-5258-44c5-8ec1-9c600d1df80b'),
  ('692a1749-e293-4bcf-8456-4fb8edc9a5a9', '{}', '4f612c5d-8b60-46bd-a2de-1f0d861264db');

INSERT INTO public.paradata_event (id, value, interrogation_id, survey_unit_id) VALUES
  ('ff45a68e-4f76-4875-b4f7-80730d3a6e35', '{"idInterrogation": "89f4df89-8e83-444f-8f43-6d964c3339d8"}', '89f4df89-8e83-444f-8f43-6d964c3339d8', '20'),
  ('cd0917a0-2239-425f-9395-5a87f875b060', '{"idInterrogation": "89f4df89-8e83-444f-8f43-6d964c3339d8"}', '89f4df89-8e83-444f-8f43-6d964c3339d8', '20'),
  ('e01d5d4b-fb01-403b-b4b3-d3841e0d12c9', '{"idInterrogation": "ae9c355c-22d3-4188-bb7e-53b7638b988b"}', 'ae9c355c-22d3-4188-bb7e-53b7638b988b', '21'),
  ('84c5e7c3-1ce5-4f9a-a192-141e9f81cd9e', '{"idInterrogation": "ae9c355c-22d3-4188-bb7e-53b7638b988b"}', 'ae9c355c-22d3-4188-bb7e-53b7638b988b', '21'),
  ('1993a133-bcd5-4c93-9eb4-12040d33fa09', '{"idInterrogation": "77ae37f6-4e51-4282-adf2-e01b15e1ef09"}', '77ae37f6-4e51-4282-adf2-e01b15e1ef09', '22'),
  ('c9f65d68-a64c-4ab9-9a33-69ce24df5920', '{"idInterrogation": "77ae37f6-4e51-4282-adf2-e01b15e1ef09"}', '77ae37f6-4e51-4282-adf2-e01b15e1ef09', '22'),
  ('408b3991-8c76-4712-aafb-c493388fb3ea', '{"idInterrogation": "b1771f46-6916-4234-94bc-6a4fcd626637"}', 'b1771f46-6916-4234-94bc-6a4fcd626637', '23'),
  ('75a27d77-009d-49eb-a3e2-044ec245b2fc', '{"idInterrogation": "b1771f46-6916-4234-94bc-6a4fcd626637"}', 'b1771f46-6916-4234-94bc-6a4fcd626637', '23'),
  ('6a422a3c-e255-4b88-b553-03a688425e43', '{"idInterrogation": "538d89c2-1047-48f7-8c16-02e9f41a8093"}', '538d89c2-1047-48f7-8c16-02e9f41a8093', 'LOG2021X11Web-01'),
  ('e9be3275-c9f7-4d56-a480-e95275ba80ca', '{"idInterrogation": "538d89c2-1047-48f7-8c16-02e9f41a8093"}', '538d89c2-1047-48f7-8c16-02e9f41a8093', 'LOG2021X11Web-01'),
  ('ae2df2f2-8b3d-4e94-83cf-053ed30b32b6', '{"idInterrogation": "d128f9bf-e933-48bd-a7e5-8892a6a92997"}', 'd128f9bf-e933-48bd-a7e5-8892a6a92997', 'LOG2021X11Web-02'),
  ('54a86bc0-f291-4d9d-9e53-bae006a6ea16', '{"idInterrogation": "d128f9bf-e933-48bd-a7e5-8892a6a92997"}', 'd128f9bf-e933-48bd-a7e5-8892a6a92997', 'LOG2021X11Web-02'),
  ('eb2b308e-bb5f-4f62-84cc-c44c4a9269de', '{"idInterrogation": "d542db08-6d58-4665-9302-56eddf8185d2"}', 'd542db08-6d58-4665-9302-56eddf8185d2', 'LOG2021X11Web-03'),
  ('a112b2f2-4724-4cbd-9fe4-bac049411cde', '{"idInterrogation": "d542db08-6d58-4665-9302-56eddf8185d2"}', 'd542db08-6d58-4665-9302-56eddf8185d2', 'LOG2021X11Web-03'),
  ('d2793581-22ab-4772-acc5-8c151cd0ec72', '{"idInterrogation": "89512ef9-4f39-466b-9388-a3626167f0c3"}', '89512ef9-4f39-466b-9388-a3626167f0c3', 'LOG2021X11Tel_01'),
  ('4608f5a2-3f1e-4231-a266-87001680aef4', '{"idInterrogation": "89512ef9-4f39-466b-9388-a3626167f0c3"}', '89512ef9-4f39-466b-9388-a3626167f0c3', 'LOG2021X11Tel_01'),
  ('c348372a-14f1-4950-8909-8bdf712acb11', '{"idInterrogation": "ec311ca8-41ec-4cbb-86ae-99745ffd93cb"}', 'ec311ca8-41ec-4cbb-86ae-99745ffd93cb', 'LOG2021X11Tel_02'),
  ('0d153c9f-e117-42e1-9b6b-3424c41c45ce', '{"idInterrogation": "ec311ca8-41ec-4cbb-86ae-99745ffd93cb"}', 'ec311ca8-41ec-4cbb-86ae-99745ffd93cb', 'LOG2021X11Tel_02'),
  ('9d38f37e-05bd-42f9-a33b-c459c257ffc5', '{"idInterrogation": "31e0537d-01ab-4402-9a59-d001d3ba00fd"}', '31e0537d-01ab-4402-9a59-d001d3ba00fd', 'LOG2021X11Tel_03'),
  ('0ad9817e-5952-4d83-b219-717fbbae860d', '{"idInterrogation": "31e0537d-01ab-4402-9a59-d001d3ba00fd"}', '31e0537d-01ab-4402-9a59-d001d3ba00fd', 'LOG2021X11Tel_03');


-- 4. personalization : adaptation du champ interrogation_id
INSERT INTO public.personalization (id, value, interrogation_id) VALUES
  ('22fda6de-c0c0-4b3c-b10c-2a2b705644c7', '[{"name": "whoAnswers1", "value": "Mr Dupond"}, {"name": "whoAnswers2", "value": ""}]', '517046b6-bd88-47e0-838e-00d03461f592'),
  ('86ee92ff-0d5d-4034-8cfb-a29f81384918', '[]', 'd98d28c2-1535-4fc8-a405-d6a554231bbc'),
  ('6fcbbd84-3464-4290-b8fc-cdf0082ee339', '[]', 'c8142dcc-c133-49aa-a969-bb9828190a2c'),
  ('f683f639-1da5-4219-95a2-2df4df0a2a0d', '[]', '45c78a3e-f3b6-4d69-bd58-d2ca749dd7cd'),
  ('65ec0765-4a5e-4518-be79-bc2be6b882b8', '[]', '89f4df89-8e83-444f-8f43-6d964c3339d8'),
  ('fc73766c-ffb7-4443-9135-1e39939320a0', '[]', 'ae9c355c-22d3-4188-bb7e-53b7638b988b'),
  ('d14499b0-93f2-4722-a624-47d7cafc26a6', '[]', '77ae37f6-4e51-4282-adf2-e01b15e1ef09'),
  ('a7e878fa-d12a-4a25-bc17-08a583b0127d', '[]', 'b1771f46-6916-4234-94bc-6a4fcd626637'),
  ('a7e878fa-d12a-4a25-bc17-08a583b0127e', '[]', '80dc2493-5258-44c5-8ec1-9c600d1df80b'),
  ('a7e878fa-d12a-4a25-bc17-08a583b0127f', '[]', '4f612c5d-8b60-46bd-a2de-1f0d861264db'),
  ('eec3ae3f-ad9e-45d6-b2f8-191e19f2a571', '[]', '538d89c2-1047-48f7-8c16-02e9f41a8093'),
  ('3cf06171-2f86-4724-8c86-fb9b2f40286a', '[]', 'd128f9bf-e933-48bd-a7e5-8892a6a92997'),
  ('1683a6ff-3c40-47c8-8862-9c685c6f5e88', '[]', 'd542db08-6d58-4665-9302-56eddf8185d2'),
  ('7c4b7cfb-7b22-43d1-84f3-07eb808a33ce', '[]', '89512ef9-4f39-466b-9388-a3626167f0c3'),
  ('9910a916-d270-47f7-9c30-19c456323831', '[]', 'ec311ca8-41ec-4cbb-86ae-99745ffd93cb'),
  ('8b377092-33da-4494-a5fc-d2825d755e2c', '[]', '31e0537d-01ab-4402-9a59-d001d3ba00fd');

-- 5. state_data : adaptation du champ interrogation_id
INSERT INTO public.state_data (id, current_page, date, state, interrogation_id) VALUES
  ('f55c7388-0724-4b3e-9c78-536ee2dee5f6', '2.3#5', 1111111111, 'EXTRACTED', '517046b6-bd88-47e0-838e-00d03461f592'),
  ('c11f8aae-5201-4a16-89d8-5f8b4c6ab942', '2.3#5', 1111111115, 'EXTRACTED', 'd98d28c2-1535-4fc8-a405-d6a554231bbc'),
  ('1fe17624-70d0-48e2-ba50-041cc23cbeeb', '2.3#5', 1111111119, 'EXTRACTED', 'c8142dcc-c133-49aa-a969-bb9828190a2c'),
  ('164cc2b6-b58f-4011-a064-01f5f761326b', '2.3#5', 1111111111, 'INIT', '45c78a3e-f3b6-4d69-bd58-d2ca749dd7cd'),
  ('42858b14-2a0c-4d17-afd0-f50a0f9a8dd5', '1', 900000000, 'INIT', '89f4df89-8e83-444f-8f43-6d964c3339d8'),
  ('a2067072-8887-4ba0-8493-d91f50734d95', '1', 900000000, 'INIT', 'ae9c355c-22d3-4188-bb7e-53b7638b988b'),
  ('1b7d2c56-aa0b-4d2a-8193-2aec2484f5ee', '1', 900000000, 'INIT', '77ae37f6-4e51-4282-adf2-e01b15e1ef09'),
  ('5cb8424f-6300-4236-86f4-ffcc90ebb6b4', '1', 900000000, 'INIT', 'b1771f46-6916-4234-94bc-6a4fcd626637'),
  ('bfe007af-29c9-4a12-a3b7-745d0a19c5f5', '1', 900000000, 'INIT', '538d89c2-1047-48f7-8c16-02e9f41a8093'),
  ('d17cd723-5674-4754-80bd-05a36235abb6', '1', 900000000, 'INIT', 'd128f9bf-e933-48bd-a7e5-8892a6a92997'),
  ('f024ca52-95e5-4d4d-a15f-6111c68ff83e', '1', 900000000, 'INIT', 'd542db08-6d58-4665-9302-56eddf8185d2'),
  ('ffa9847f-c2fa-4b50-a017-afcbf6a9c205', '1', 900000000, 'INIT', '89512ef9-4f39-466b-9388-a3626167f0c3'),
  ('9affeeb6-84f8-4f8c-bc7d-31b27202c8ab', '1', 900000000, 'INIT', 'ec311ca8-41ec-4cbb-86ae-99745ffd93cb'),
  ('e75e53d5-66a4-4ab9-922a-a84e5709e8c9', '1', 900000000, 'INIT', '31e0537d-01ab-4402-9a59-d001d3ba00fd');

--
-- TOC entry 3410 (class 0 OID 16540)
-- Dependencies: 223
-- Data for Name: interrogation_temp_zone; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.interrogation_temp_zone(id, interrogation_id, user_id, "date", interrogation) VALUES
  ('42858b14-2a0c-4d17-afd0-f50a0f9a8dd5', '517046b6-bd88-47e0-838e-00d03461f592', 'user-id', 900000000, '{"data": {"EXTERNAL": {"ADR": "Rue des Plantes","NUMTH": "1"}},"comment": {},"personalization": [],"questionnaireId": "questionnaire-11"}'),
  ('6fcbbd84-3464-4290-b8fc-cdf0082ee339', 'd98d28c2-1535-4fc8-a405-d6a554231bbc', 'user-id', 900000000, '{"data": {"EXTERNAL": {"ADR": "Rue des Plantes","NUMTH": "1"}},"comment": {},"personalization": [],"questionnaireId": "questionnaire-12"}');


--changeset davdarras:test-fun1-data context:test

-- quite ugly here. need to have the ' as delimiter for @Sql annotation (hibernate problem)
-- maybe switch to liquibase migration in IT intead of using @Sql annotation
CREATE OR REPLACE FUNCTION encrypt(data_text text) RETURNS bytea
AS
'
BEGIN
    RETURN pgp_sym_encrypt(data_text, current_setting(''data.encryption.key'', true), ''s2k-count=65536'');
END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION is_encrypted()
RETURNS integer AS
'
BEGIN
  IF current_setting(''data.encryption.key'', true) IS NOT NULL THEN
    RETURN 1;
  ELSE
    RETURN 0;
  END IF;
END;
' LANGUAGE plpgsql;


DO '
DECLARE
  -- Define array with all lines to insert
  datas_to_insert text[][] := ARRAY[
      ARRAY[''6cb4378d-aa70-4add-bb61-1f2fdc86dfbb'',
            ''{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}, "COLLECTED": {"READY": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": true}, "COMMENT": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Love it !"}, "PRODUCER": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Matt Groening"}}}'',
            ''517046b6-bd88-47e0-838e-00d03461f592''],
      ARRAY[''cf72a231-b40f-4ffa-9834-bf4e40bf85ac'',
            ''{}'',
            ''d98d28c2-1535-4fc8-a405-d6a554231bbc''],
      ARRAY[''f63bbfbe-9926-48ae-8d04-421296a40634'',
            ''{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}, "COLLECTED": {"READY": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": true}, "COMMENT": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Love it !"}, "PRODUCER": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Matt Groening"}}}'',
            ''c8142dcc-c133-49aa-a969-bb9828190a2c''],
      ARRAY[''8e3b28cc-74b1-4391-8359-c495538129b7'',
            ''{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}, "COLLECTED": {"READY": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": true}, "COMMENT": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Love it !"}, "PRODUCER": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Matt Groening"}}}'',
            ''45c78a3e-f3b6-4d69-bd58-d2ca749dd7cd''],
      ARRAY[''e9e97450-ef9c-4f49-9375-adf11b6a158b'',
            ''{}'',
            ''89f4df89-8e83-444f-8f43-6d964c3339d8''],
      ARRAY[''42dc1400-0a36-4c20-8742-115e22c42369'',
            ''{}'',
            ''ae9c355c-22d3-4188-bb7e-53b7638b988b''],
      ARRAY[''4540afba-ee51-42e4-bf74-d2346d813e89'',
            ''{}'',
            ''77ae37f6-4e51-4282-adf2-e01b15e1ef09''],
      ARRAY[''757170c2-b2d5-4c71-85c1-61988b36e416'',
            ''{}'',
            ''b1771f46-6916-4234-94bc-6a4fcd626637''],
      ARRAY[''757170c2-b2d5-4c71-85c1-61988b36e417'',
            ''{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}, "COLLECTED": {"PRODUCER": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": "Matt Groening"}, "READY": {"EDITED": null, "FORCED": null, "INPUTED": null, "PREVIOUS": null, "COLLECTED": true}}}'',
            ''80dc2493-5258-44c5-8ec1-9c600d1df80b''],
      ARRAY[''757170c2-b2d5-4c71-85c1-61988b36e418'',
            ''{"EXTERNAL": {"LAST_BROADCAST": "12/07/1998"}}'',
            ''4f612c5d-8b60-46bd-a2de-1f0d861264db''],
      ARRAY[''27abfaed-187a-44ab-8287-af08f3bd7158'',
            ''{}'',
            ''538d89c2-1047-48f7-8c16-02e9f41a8093''],
      ARRAY[''c118114a-c0be-462d-9fe9-604436bea20a'',
            ''{}'',
            ''d128f9bf-e933-48bd-a7e5-8892a6a92997''],
      ARRAY[''df044ba3-9abb-451e-9e4d-75ba98ace5e6'',
            ''{}'',
            ''d542db08-6d58-4665-9302-56eddf8185d2''],
      ARRAY[''fdc43238-cf8e-4a55-ad49-14ea8152728d'',
            ''{}'',
            ''89512ef9-4f39-466b-9388-a3626167f0c3''],
      ARRAY[''fa0a7a90-0324-429f-837b-ad06b79cfd7d'',
            ''{}'',
            ''ec311ca8-41ec-4cbb-86ae-99745ffd93cb''],
      ARRAY[''d51e29b9-b27b-4159-957c-6bb54d811a20'',
            ''{}'',
            ''31e0537d-01ab-4402-9a59-d001d3ba00fd'']
  ];
  line text[];
BEGIN
  FOREACH line SLICE 1 IN ARRAY datas_to_insert LOOP
    -- line[1] = id, line[2] = json value, line[3] = interrogation_id.
    IF is_encrypted() = 1 THEN
      INSERT INTO public.data (id, value, interrogation_id, encrypted) VALUES ( line[1]::uuid, encrypt(line[2]), line[3], is_encrypted());
    ELSE
      INSERT INTO public.data (id, value, interrogation_id, encrypted) VALUES ( line[1]::uuid, line[2]::jsonb, line[3], is_encrypted());
    END IF;
  END LOOP;
END ';