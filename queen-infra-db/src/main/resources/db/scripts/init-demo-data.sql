--changeset davdarras:demo-data context:demo

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
  ('LOG2021X11Tel', 'Enquête Logement 2022 - Séquence 1 - HR', 'NORMAL'),
  ('AQV2022X00', 'Campagne qualité volaille en 2022', 'NORMAL'),
  ('AQV2023X00', 'Campagne qualité volaille en 2023', 'SENSITIVE'),
  ('AQV2024X00', 'Campagne qualité volaille en 2024', 'NORMAL');


--
-- TOC entry 3406 (class 0 OID 16489)
-- Dependencies: 219
-- Data for Name: metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.metadata(id, value, campaign_id) VALUES 
  ('6ce93fc8-1abd-4da3-b251-805943948954', '{}', 'SIMPSONS2020X00'),
  ('0fb58fa2-e26a-4a68-9ca7-6ec63bb2fb71', '{}', 'VQS2021X00'),
  ('09a6cf03-2998-4451-9cc0-522b7c7f423a', '{  "logos": [{"url": "https://insee.fr/logo1.png","label": "logo1"},{"url":"https://insee.fr/logo2.png","label":"logo2"},{"url":"https://insee.fr/logo3.png","label":"logo3"}],"variables": [{"name": "Enq_LibelleEnquete", "value": "Enquête logement pour la recette technique"}, {"name": "Enq_ObjectifsCourts", "value": "Cette enquête permet de connaître votre logement mais surtout nos applis"}, {"name": "Enq_CaractereObligatoire", "value": true}, {"name": "Enq_NumeroVisa", "value": "2021A054EC"}, {"name": "Enq_MinistereTutelle", "value": "de l''Économie, des Finances et de la Relance"}, {"name": "Enq_ParutionJo", "value": true}, {"name": "Enq_DateParutionJo", "value": "23/11/2020"}, {"name": "Enq_RespOperationnel", "value": "L’Institut national de la statistique et des études économiques (Insee)"}, {"name": "Enq_RespTraitement", "value": "l''Insee"}, {"name": "Enq_AnneeVisa", "value": "2021"}, {"name": "Loi_statistique", "value": "https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000888573"}, {"name": "Loi_rgpd", "value": "https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=CELEX%3A32016R0679"}, {"name": "Loi_informatique", "value": "https://www.legifrance.gouv.fr/affichTexte.do?cidTexte=JORFTEXT000000886460"}], "inseeContext": "household"}', 'LOG2021X11Web'),
  ('186d53db-a653-44f2-abda-f0f1d1cddfe2', '{}', 'LOG2021X11Tel'),
  ('186d53db-a653-44f2-abda-f1f1d1cddfe2', '{}', 'AQV2023X00'),
  ('186d53db-a653-44f2-abda-f2f1d1cddfe2', '{}', 'AQV2024X00'),
  ('186d53db-a653-44f2-abda-f3f1d1cddfe2', '{}', 'AQV2022X00');

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

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'QmWithoutCamp',
  'Questionnaire with no campaign',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  NULL
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'simpsons',
  'Questionnaire about the Simpsons tv show',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'SIMPSONS2020X00'
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'simpsonsV2',
  'Questionnaire about the Simpsons tv show version 2',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'SIMPSONS2020X00'
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'VQS2021X00',
  'Questionnaire of the Everyday life and health survey 2021',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'VQS2021X00'
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'LOG2021X11Web',
  'Enquête Logement 2022 - Séquence 1 - HR - Web',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'LOG2021X11Web'
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'LOG2021X11Tel',
  'Enquête Logement 2022 - Séquence 1 - HR',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'LOG2021X11Tel'
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'aqv2022x00',
  'qualité volaille en 2022',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'AQV2022X00'
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'aqv2023x00',
  'qualité volaille en 2022',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'AQV2023X00'
);

INSERT INTO public.questionnaire_model(id, label, value, campaign_id) VALUES (
  'aqv2024x00',
  'qualité volaille en 2022',
  $${
  "id": "m6kllxkx",
  "label": {
    "type": "VTL|MD",
    "value": "test pattern - QR"
  },
  "modele": "TESTPATTERQR",
  "maxPage": "2",
  "resizing": {},
  "variables": [
    {
      "name": "TEXT",
      "values": {
        "EDITED": null,
        "FORCED": null,
        "INPUTTED": null,
        "PREVIOUS": null,
        "COLLECTED": null
      },
      "dimension": 0,
      "variableType": "COLLECTED"
    },
    {
      "name": "FILTER_RESULT_TEXT",
      "dimension": 0,
      "expression": {
        "type": "VTL",
        "value": "true"
      },
      "variableType": "CALCULATED"
    }
  ],
  "components": [
    {
      "id": "m6klho5q",
      "page": "1",
      "label": {
        "type": "VTL",
        "value": "\"I - \" || \"S1\""
      },
      "componentType": "Sequence",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    },
    {
      "id": "question-m6klhuk3",
      "page": "2",
      "label": {
        "type": "VTL|MD",
        "value": "\"question texte\""
      },
      "components": [
        {
          "id": "m6klhuk3",
          "page": "2",
          "response": {
            "name": "TEXT"
          },
          "mandatory": false,
          "maxLength": 249,
          "componentType": "Input"
        }
      ],
      "componentType": "Question",
      "conditionFilter": {
        "type": "VTL",
        "value": "true"
      }
    }
  ],
  "pagination": "question",
  "componentType": "Questionnaire",
  "enoCoreVersion": "3.33.0-SNAPSHOT.1",
  "generatingDate": "13-02-2025 13:41:02",
  "lunaticModelVersion": "3.15.3"
}$$,
  'AQV2024X00'
);

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
  ('LOG2021X11Tel', 'L_NATIONETR'),
  ('aqv2022x00', 'L_DEPNAIS'),
  ('aqv2022x00', 'L_NATIONETR'),
  ('aqv2023x00', 'L_DEPNAIS'),
  ('aqv2023x00', 'L_NATIONETR'),
  ('aqv2024x00', 'L_DEPNAIS'),
  ('aqv2024x00', 'L_NATIONETR');

--
-- TOC entry 3404 (class 0 OID 16424)
-- Dependencies: 217
-- Data for Name: interrogation; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.interrogation (id, survey_unit_id, campaign_id, questionnaire_model_id) VALUES
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdb1', '11', 'SIMPSONS2020X00', 'simpsons'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdb2', '12', 'SIMPSONS2020X00', 'simpsons'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdb3', '13', 'SIMPSONS2020X00', 'simpsonsV2'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdb4', '14', 'SIMPSONS2020X00', 'simpsonsV2'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0', '20', 'VQS2021X00', 'VQS2021X00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1', '21', 'VQS2021X00', 'VQS2021X00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2', '22', 'VQS2021X00', 'VQS2021X00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3', '23', 'VQS2021X00', 'VQS2021X00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1', 'LOG2021X11Web-01', 'LOG2021X11Web', 'LOG2021X11Web'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2', 'LOG2021X11Web-02', 'LOG2021X11Web', 'LOG2021X11Web'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3', 'LOG2021X11Web-03', 'LOG2021X11Web', 'LOG2021X11Web'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbde1', 'LOG2021X11Tel_01', 'LOG2021X11Tel', 'LOG2021X11Tel'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbde2', 'LOG2021X11Tel_02', 'LOG2021X11Tel', 'LOG2021X11Tel'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbde3', 'LOG2021X11Tel_03', 'LOG2021X11Tel', 'LOG2021X11Tel'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdf0', 'su-test-diff-data', 'LOG2021X11Tel', 'LOG2021X11Tel'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbdf1', 'su-test-diff-without-collected-data', 'LOG2021X11Tel', 'LOG2021X11Tel'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd01', 'PROTO01', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd02', 'PROTO02', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd03', 'PROTO03', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd04', 'PROTO04', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd05', 'PROTO05', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd06', 'PROTO06', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd07', 'PROTO07', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd08', 'PROTO08', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd09', 'PROTO09', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd10', 'PROTO10', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd11', 'PROTO11', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd12', 'PROTO12', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd13', 'PROTO13', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd14', 'PROTO14', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd15', 'PROTO15', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd16', 'PROTO16', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd17', 'PROTO17', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd18', 'PROTO18', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd19', 'PROTO19', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd20', 'PROTO20', 'AQV2023X00', 'aqv2023x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd21', 'PROTO21', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd22', 'PROTO22', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd23', 'PROTO23', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd24', 'PROTO24', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd25', 'PROTO25', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd26', 'PROTO26', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd27', 'PROTO27', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd28', 'PROTO28', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd29', 'PROTO29', 'AQV2024X00', 'aqv2024x00'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd30', 'PROTO30', 'AQV2024X00', 'aqv2024x00');



--
-- TOC entry 3399 (class 0 OID 16396)
-- Dependencies: 212
-- Data for Name: comment; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.comment(id, value, interrogation_id) VALUES 
  ('a78366f8-8653-448a-8754-53a3135a2137', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb1'),
  ('7d87278a-c6fc-4989-829c-9867864ef74b', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb2'),
  ('67945983-430a-4b34-99e4-86ee75e0e27a', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb3'),
  ('56bfe7ef-1ea6-49a4-bd37-d46ddee36fcc', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb4'),
  ('ee175940-8f81-4d52-8e72-1734e8f3f0fe', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0'),
  ('86f7a230-1408-4569-b663-9817840ab7da', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1'),
  ('c0bc2f92-be6b-4ebd-b353-a04d05f6d314', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2'),
  ('692a1749-e293-4bcf-8456-4fb8edc9a5a7', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3'),
  ('53912a39-0e87-4f3c-804b-32910a2c1e6e', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1'),
  ('c42ebc95-f35c-4a34-be61-5b1e9c1a37fe', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2'),
  ('d8b683e0-850e-487f-bc8d-6f3c9440e32b', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3'),
  ('d6f92b88-0b80-41dc-a1b3-e69b2fb71846', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbde1'),
  ('70ee3af3-fd2c-4745-b0bb-73124fa016b8', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbde2'),
  ('833b5a5d-845e-4b3e-a725-d444907ee476', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbde3'),
  ('692a1749-e293-4bcf-8456-4fb8edc9a5a8', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdf0'),
  ('692a1749-e293-4bcf-8456-4fb8edc9a5a9', '{}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdf1'),
  ('b78366f8-8653-448a-8754-53a3135a2130', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd01'),
  ('b78366f8-8653-448a-8754-53a3135a2131', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd02'),
  ('b78366f8-8653-448a-8754-53a3135a2132', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd03'),
  ('b78366f8-8653-448a-8754-53a3135a2133', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd04'),
  ('b78366f8-8653-448a-8754-53a3135a2134', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd05'),
  ('b78366f8-8653-448a-8754-53a3135a2135', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd06'),
  ('b78366f8-8653-448a-8754-53a3135a2136', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd07'),
  ('b78366f8-8653-448a-8754-53a3135a2137', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'),
  ('b78366f8-8653-448a-8754-53a3135a2138', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'),
  ('b78366f8-8653-448a-8754-53a3135a2140', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'),
  ('b78366f8-8653-448a-8754-53a3135a2141', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd11'),
  ('b78366f8-8653-448a-8754-53a3135a2142', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd12'),
  ('b78366f8-8653-448a-8754-53a3135a2143', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd13'),
  ('b78366f8-8653-448a-8754-53a3135a2144', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd14'),
  ('b78366f8-8653-448a-8754-53a3135a2145', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd15'),
  ('b78366f8-8653-448a-8754-53a3135a2146', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd16'),
  ('b78366f8-8653-448a-8754-53a3135a2147', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd17'),
  ('b78366f8-8653-448a-8754-53a3135a2148', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd18'),
  ('b78366f8-8653-448a-8754-53a3135a2149', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd19'),
  ('b78366f8-8653-448a-8754-53a3135a2150', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd20'),
  ('b78366f8-8653-448a-8754-53a3135a2151', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd21'),
  ('b78366f8-8653-448a-8754-53a3135a2152', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd22'),
  ('b78366f8-8653-448a-8754-53a3135a2153', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd23'),
  ('b78366f8-8653-448a-8754-53a3135a2154', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd24'),
  ('b78366f8-8653-448a-8754-53a3135a2155', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd25'),
  ('b78366f8-8653-448a-8754-53a3135a2156', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd26'),
  ('b78366f8-8653-448a-8754-53a3135a2157', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd27'),
  ('b78366f8-8653-448a-8754-53a3135a2158', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd28'),
  ('b78366f8-8653-448a-8754-53a3135a2159', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd29'),
  ('b78366f8-8653-448a-8754-53a3135a2160', '{"COMMENT": "un commentaire"}', '0c83fb82-0197-7197-8e8c-a6ce2c2dbd30');

--
-- TOC entry 3407 (class 0 OID 16494)
-- Dependencies: 220
-- Data for Name: paradata_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.paradata_event(id, value, survey_unit_id, interrogation_id) VALUES
  -- premières données existantes
  ('ff45a68e-4f76-4875-b4f7-80730d3a6e35','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0"}','20','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0'),
  ('cd0917a0-2239-425f-9395-5a87f875b060','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0"}','20','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0'),
  ('e01d5d4b-fb01-403b-b4b3-d3841e0d12c9','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1"}','21','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1'),
  ('84c5e7c3-1ce5-4f9a-a192-141e9f81cd9e','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1"}','21','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1'),
  ('1993a133-bcd5-4c93-9eb4-12040d33fa09','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2"}','22','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2'),
  ('c9f65d68-a64c-4ab9-9a33-69ce24df5920','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2"}','22','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2'),
  ('408b3991-8c76-4712-aafb-c493388fb3ea','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3"}','23','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3'),
  ('75a27d77-009d-49eb-a3e2-044ec245b2fc','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3"}','23','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3'),
  ('6a422a3c-e255-4b88-b553-03a688425e43','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1"}','LOG2021X11Web-01','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1'),
  ('e9be3275-c9f7-4d56-a480-e95275ba80ca','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1"}','LOG2021X11Web-01','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1'),
  ('ae2df2f2-8b3d-4e94-83cf-053ed30b32b6','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2"}','LOG2021X11Web-02','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2'),
  ('54a86bc0-f291-4d9d-9e53-bae006a6ea16','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2"}','LOG2021X11Web-02','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2'),
  ('eb2b308e-bb5f-4f62-84cc-c44c4a9269de','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3"}','LOG2021X11Web-03','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3'),
  ('a112b2f2-4724-4cbd-9fe4-bac049411cde','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3"}','LOG2021X11Web-03','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3'),
  ('d2793581-22ab-4772-acc5-8c151cd0ec72','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbde1"}','LOG2021X11Tel_01','0c83fb82-0197-7197-8e8c-a6ce2c2dbde1'),
  ('4608f5a2-3f1e-4231-a266-87001680aef4','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbde1"}','LOG2021X11Tel_01','0c83fb82-0197-7197-8e8c-a6ce2c2dbde1'),
  ('c348372a-14f1-4950-8909-8bdf712acb11','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbde2"}','LOG2021X11Tel_02','0c83fb82-0197-7197-8e8c-a6ce2c2dbde2'),
  ('0d153c9f-e117-42e1-9b6b-3424c41c45ce','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbde2"}','LOG2021X11Tel_02','0c83fb82-0197-7197-8e8c-a6ce2c2dbde2'),
  ('9d38f37e-05bd-42f9-a33b-c459c257ffc5','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbde3"}','LOG2021X11Tel_03','0c83fb82-0197-7197-8e8c-a6ce2c2dbde3'),
  ('0ad9817e-5952-4d83-b219-717fbbae860d','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbde3"}','LOG2021X11Tel_03','0c83fb82-0197-7197-8e8c-a6ce2c2dbde3'),
  ('b78366f8-8653-448a-8754-53a3135a2130','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd01"}','PROTO01','0c83fb82-0197-7197-8e8c-a6ce2c2dbd01'),
  ('b78366f8-8653-448a-8754-53a3135a2131','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd02"}','PROTO02','0c83fb82-0197-7197-8e8c-a6ce2c2dbd02'),
  ('b78366f8-8653-448a-8754-53a3135a2132','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd03"}','PROTO03','0c83fb82-0197-7197-8e8c-a6ce2c2dbd03'),
  ('b78366f8-8653-448a-8754-53a3135a2133','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd04"}','PROTO04','0c83fb82-0197-7197-8e8c-a6ce2c2dbd04'),
  ('b78366f8-8653-448a-8754-53a3135a2134','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd05"}','PROTO05','0c83fb82-0197-7197-8e8c-a6ce2c2dbd05'),
  ('b78366f8-8653-448a-8754-53a3135a2135','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd06"}','PROTO06','0c83fb82-0197-7197-8e8c-a6ce2c2dbd06'),
  ('b78366f8-8653-448a-8754-53a3135a2136','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd07"}','PROTO07','0c83fb82-0197-7197-8e8c-a6ce2c2dbd07'),
  ('b78366f8-8653-448a-8754-53a3135a2137','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd08"}','PROTO08','0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'),
  ('b78366f8-8653-448a-8754-53a3135a2138','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd09"}','PROTO09','0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'),
  ('b78366f8-8653-448a-8754-53a3135a2140','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd10"}','PROTO10','0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'),
  ('b78366f8-8653-448a-8754-53a3135a2141','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd11"}','PROTO11','0c83fb82-0197-7197-8e8c-a6ce2c2dbd11'),
  ('b78366f8-8653-448a-8754-53a3135a2142','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd12"}','PROTO12','0c83fb82-0197-7197-8e8c-a6ce2c2dbd12'),
  ('b78366f8-8653-448a-8754-53a3135a2143','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd13"}','PROTO13','0c83fb82-0197-7197-8e8c-a6ce2c2dbd13'),
  ('b78366f8-8653-448a-8754-53a3135a2144','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd14"}','PROTO14','0c83fb82-0197-7197-8e8c-a6ce2c2dbd14'),
  ('b78366f8-8653-448a-8754-53a3135a2145','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd15"}','PROTO15','0c83fb82-0197-7197-8e8c-a6ce2c2dbd15'),
  ('b78366f8-8653-448a-8754-53a3135a2146','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd16"}','PROTO16','0c83fb82-0197-7197-8e8c-a6ce2c2dbd16'),
  ('b78366f8-8653-448a-8754-53a3135a2147','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd17"}','PROTO17','0c83fb82-0197-7197-8e8c-a6ce2c2dbd17'),
  ('b78366f8-8653-448a-8754-53a3135a2148','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd18"}','PROTO18','0c83fb82-0197-7197-8e8c-a6ce2c2dbd18'),
  ('b78366f8-8653-448a-8754-53a3135a2149','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd19"}','PROTO19','0c83fb82-0197-7197-8e8c-a6ce2c2dbd19'),
  ('b78366f8-8653-448a-8754-53a3135a2150','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd20"}','PROTO20','0c83fb82-0197-7197-8e8c-a6ce2c2dbd20'),
  ('b78366f8-8653-448a-8754-53a3135a2151','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd21"}','PROTO21','0c83fb82-0197-7197-8e8c-a6ce2c2dbd21'),
  ('b78366f8-8653-448a-8754-53a3135a2152','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd22"}','PROTO22','0c83fb82-0197-7197-8e8c-a6ce2c2dbd22'),
  ('b78366f8-8653-448a-8754-53a3135a2153','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd23"}','PROTO23','0c83fb82-0197-7197-8e8c-a6ce2c2dbd23'),
  ('b78366f8-8653-448a-8754-53a3135a2154','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd24"}','PROTO24','0c83fb82-0197-7197-8e8c-a6ce2c2dbd24'),
  ('b78366f8-8653-448a-8754-53a3135a2155','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd25"}','PROTO25','0c83fb82-0197-7197-8e8c-a6ce2c2dbd25'),
  ('b78366f8-8653-448a-8754-53a3135a2156','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd26"}','PROTO26','0c83fb82-0197-7197-8e8c-a6ce2c2dbd26'),
  ('b78366f8-8653-448a-8754-53a3135a2157','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd27"}','PROTO27','0c83fb82-0197-7197-8e8c-a6ce2c2dbd27'),
  ('b78366f8-8653-448a-8754-53a3135a2158','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd28"}','PROTO28','0c83fb82-0197-7197-8e8c-a6ce2c2dbd28'),
  ('b78366f8-8653-448a-8754-53a3135a2159','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd29"}','PROTO29','0c83fb82-0197-7197-8e8c-a6ce2c2dbd29'),
  ('b78366f8-8653-448a-8754-53a3135a2160','{"idInterrogation":"0c83fb82-0197-7197-8e8c-a6ce2c2dbd30"}','PROTO30','0c83fb82-0197-7197-8e8c-a6ce2c2dbd30');


--
-- TOC entry 3408 (class 0 OID 16499)
-- Dependencies: 221
-- Data for Name: personalization; Type: TABLE DATA; Schema: public; Owner: postgres
--

-- Personalization avec les bons interrogation_id selon public.interrogation
INSERT INTO public.personalization (id, value, interrogation_id) VALUES
  ('22fda6de-c0c0-4b3c-b10c-2a2b705644c7', '[{"name":"whoAnswers1","value":"Mr Dupond"},{"name":"whoAnswers2","value":""}]', '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb1'),  -- 11
  ('86ee92ff-0d5d-4034-8cfb-a29f81384918','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb2'),  -- 12
  ('6fcbbd84-3464-4290-b8fc-cdf0082ee339','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb3'),  -- 13
  ('f683f639-1da5-4219-95a2-2df4df0a2a0d','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb4'),  -- 14
  ('65ec0765-4a5e-4518-be79-bc2be6b882b8','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0'),  -- 20
  ('fc73766c-ffb7-4443-9135-1e39939320a0','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1'),  -- 21
  ('d14499b0-93f2-4722-a624-47d7cafc26a6','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2'),  -- 22
  ('a7e878fa-d12a-4a25-bc17-08a583b0127d','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3'),  -- 23
  ('a7e878fa-d12a-4a25-bc17-08a583b0127e','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdf0'),  -- su-test-diff-data
  ('a7e878fa-d12a-4a25-bc17-08a583b0127f','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdf1'),  -- su-test-diff-without-collected-data
  ('eec3ae3f-ad9e-45d6-b2f8-191e19f2a571','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1'),  -- LOG2021X11Web-01
  ('3cf06171-2f86-4724-8c86-fb9b2f40286a','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2'),  -- LOG2021X11Web-02
  ('1683a6ff-3c40-47c8-8862-9c685c6f5e88','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3'),  -- LOG2021X11Web-03
  ('7c4b7cfb-7b22-43d1-84f3-07eb808a33ce','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbde1'),  -- LOG2021X11Tel_01
  ('9910a916-d270-47f7-9c30-19c456323831','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbde2'),  -- LOG2021X11Tel_02
  ('8b377092-33da-4494-a5fc-d2825d755e2c','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbde3'),  -- LOG2021X11Tel_03
  ('b78366f8-8653-448a-8754-53a3135a2130','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd01'), -- PROTO01
  ('b78366f8-8653-448a-8754-53a3135a2131','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd02'), -- PROTO02
  ('b78366f8-8653-448a-8754-53a3135a2132','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd03'), -- PROTO03
  ('b78366f8-8653-448a-8754-53a3135a2133','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd04'), -- PROTO04
  ('b78366f8-8653-448a-8754-53a3135a2134','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd05'), -- PROTO05
  ('b78366f8-8653-448a-8754-53a3135a2135','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd06'), -- PROTO06
  ('b78366f8-8653-448a-8754-53a3135a2136','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd07'), -- PROTO07
  ('b78366f8-8653-448a-8754-53a3135a2137','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'), -- PROTO08
  ('b78366f8-8653-448a-8754-53a3135a2138','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'), -- PROTO09
  ('b78366f8-8653-448a-8754-53a3135a2140','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'), -- PROTO10
  ('b78366f8-8653-448a-8754-53a3135a2141','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd11'), -- PROTO11
  ('b78366f8-8653-448a-8754-53a3135a2142','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd12'), -- PROTO12
  ('b78366f8-8653-448a-8754-53a3135a2143','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd13'), -- PROTO13
  ('b78366f8-8653-448a-8754-53a3135a2144','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd14'), -- PROTO14
  ('b78366f8-8653-448a-8754-53a3135a2145','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd15'), -- PROTO15
  ('b78366f8-8653-448a-8754-53a3135a2146','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd16'), -- PROTO16
  ('b78366f8-8653-448a-8754-53a3135a2147','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd17'), -- PROTO17
  ('b78366f8-8653-448a-8754-53a3135a2148','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd18'), -- PROTO18
  ('b78366f8-8653-448a-8754-53a3135a2149','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd19'), -- PROTO19
  ('b78366f8-8653-448a-8754-53a3135a2150','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd20'), -- PROTO20
  ('b78366f8-8653-448a-8754-53a3135a2151','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd21'), -- PROTO21
  ('b78366f8-8653-448a-8754-53a3135a2152','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd22'), -- PROTO22
  ('b78366f8-8653-448a-8754-53a3135a2153','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd23'), -- PROTO23
  ('b78366f8-8653-448a-8754-53a3135a2154','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd24'), -- PROTO24
  ('b78366f8-8653-448a-8754-53a3135a2155','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd25'), -- PROTO25
  ('b78366f8-8653-448a-8754-53a3135a2156','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd26'), -- PROTO26
  ('b78366f8-8653-448a-8754-53a3135a2157','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd27'), -- PROTO27
  ('b78366f8-8653-448a-8754-53a3135a2158','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd28'), -- PROTO28
  ('b78366f8-8653-448a-8754-53a3135a2159','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd29'), -- PROTO29
  ('b78366f8-8653-448a-8754-53a3135a2160','[]','0c83fb82-0197-7197-8e8c-a6ce2c2dbd30'); -- PROTO30

INSERT INTO public.state_data(id, current_page, date, state, interrogation_id) VALUES
  ('f55c7388-0724-4b3e-9c78-536ee2dee5f6','2.3#5',1731165855001,'EXTRACTED','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb1'),
  ('c11f8aae-5201-4a16-89d8-5f8b4c6ab942','2.3#5',1731165855002,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb2'),
  ('1fe17624-70d0-48e2-ba50-041cc23cbeeb','2.3#5',1731165855003,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb3'),
  ('164cc2b6-b58f-4011-a064-01f5f761326b','2.3#5',1731165855004,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb4'),
  ('42858b14-2a0c-4d17-afd0-f50a0f9a8dd5','1',1731165855005,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0'),
  ('a2067072-8887-4ba0-8493-d91f50734d95','2',1731165855006,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1'),
  ('1b7d2c56-aa0b-4d2a-8193-2aec2484f5ee','3',1731165855007,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2'),
  ('5cb8424f-6300-4236-86f4-ffcc90ebb6b4','1',1731165855008,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3'),
  ('bfe007af-29c9-4a12-a3b7-745d0a19c5f5','1',1731165855009,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1'),
  ('d17cd723-5674-4754-80bd-05a36235abb6','1',1731165855010,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2'),
  ('f024ca52-95e5-4d4d-a15f-6111c68ff83e','1',1731165855011,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3'),
  ('ffa9847f-c2fa-4b50-a017-afcbf6a9c205','1',1731165855012,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbde1'),
  ('9affeeb6-84f8-4f8c-bc7d-31b27202c8ab','1',1731165855013,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbde2'),
  ('e75e53d5-66a4-4ab9-922a-a84e5709e8c9','1',1731165855014,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbde3'),
  ('b78366f8-8653-448a-8754-53a3135a2131','1',1741179158000,'VALIDATED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd02'),
  ('b78366f8-8653-448a-8754-53a3135a2132','1',1741179158001,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd03'),
  ('b78366f8-8653-448a-8754-53a3135a2134','2',1741179158002,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd05'),
  ('b78366f8-8653-448a-8754-53a3135a2135','1',1741179158003,'EXTRACTED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd06'),
  ('b78366f8-8653-448a-8754-53a3135a2136','4',1741179158004,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd07'),
  ('b78366f8-8653-448a-8754-53a3135a2137','1',1741179158005,'EXTRACTED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'),
  ('b78366f8-8653-448a-8754-53a3135a2138','5',1741179158006,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'),
  ('b78366f8-8653-448a-8754-53a3135a2140','6',1741179158007,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'),
  ('b78366f8-8653-448a-8754-53a3135a2141','7',1741179158008,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd11'),
  ('b78366f8-8653-448a-8754-53a3135a2142','8',1741179158009,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd12'),
  ('b78366f8-8653-448a-8754-53a3135a2143','1',1741179158010,'VALIDATED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd13'),
  ('b78366f8-8653-448a-8754-53a3135a2144','10',1741179158011,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd14'),
  ('b78366f8-8653-448a-8754-53a3135a2145','11',1741179158012,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd15'),
  ('b78366f8-8653-448a-8754-53a3135a2146','1',1741179158013,'VALIDATED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd16'),
  ('b78366f8-8653-448a-8754-53a3135a2147','13',1741179158014,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd17'),
  ('b78366f8-8653-448a-8754-53a3135a2148','14',1741179158015,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd18'),
  ('b78366f8-8653-448a-8754-53a3135a2149','1',1741179158016,'EXTRACTED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd19'),
  ('b78366f8-8653-448a-8754-53a3135a2150','2.1#2',1741179158017,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd20'),
  ('b78366f8-8653-448a-8754-53a3135a2151','1',1741179158018,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd21'),
  ('b78366f8-8653-448a-8754-53a3135a2152','1',1741179158019,'VALIDATED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd22'),
  ('b78366f8-8653-448a-8754-53a3135a2153','1',1741179158020,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd23'),
  ('b78366f8-8653-448a-8754-53a3135a2154','1',1741179158021,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd24'),
  ('b78366f8-8653-448a-8754-53a3135a2155','1',1741179158022,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd25'),
  ('b78366f8-8653-448a-8754-53a3135a2156','1',1741179158023,'EXTRACTED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd26'),
  ('b78366f8-8653-448a-8754-53a3135a2157','1',1741179158024,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd27'),
  ('b78366f8-8653-448a-8754-53a3135a2158','1',1741179158025,'EXTRACTED','0c83fb82-0197-7197-8e8c-a6ce2c2dbd28'),
  ('b78366f8-8653-448a-8754-53a3135a2159','1',1741179158026,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd29'),
  ('b78366f8-8653-448a-8754-53a3135a2160','1',1741179158027,'INIT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd30');

--
-- TOC entry 3410 (class 0 OID 16540)
-- Dependencies: 223
-- Data for Name: interrogation_temp_zone; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.interrogation_temp_zone(id, interrogation_id, user_id, date, interrogation) VALUES 
  ('42858b14-2a0c-4d17-afd0-f50a0f9a8dd5','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb2', 'user-id', 1741179158017, '{"data": {"EXTERNAL": {"ADR": "Rue des Plantes","NUMTH": "1"}},"comment": {},"personalization": [],"questionnaireId": "questionnaire-11"}'),
  ('6fcbbd84-3464-4290-b8fc-cdf0082ee339','0c83fb82-0197-7197-8e8c-a6ce2c2dbdb3', 'user-id', 1741179158017, '{"data": {"EXTERNAL": {"ADR": "Rue des Plantes","NUMTH": "1"}},"comment": {},"personalization": [],"questionnaireId": "questionnaire-12"}');

--
-- TOC entry 3400 (class 0 OID 16403)
-- Dependencies: 213
-- Data for Name: data; Type: TABLE DATA; Schema: public; Owner: postgres
--

CREATE OR REPLACE FUNCTION encrypt(data_text text)
RETURNS bytea AS $$
BEGIN
    RETURN pgp_sym_encrypt(data_text, current_setting('data.encryption.key', true), 's2k-count=65536');
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION is_encrypted()
RETURNS integer AS $$
BEGIN
  IF current_setting('data.encryption.key', true) IS NOT NULL THEN
    RETURN 1;
  ELSE
    RETURN 0;
  END IF;
END;
$$ LANGUAGE plpgsql;

DO $$
DECLARE
  -- Définition du tableau contenant toutes les lignes à insérer.
  datas_to_insert text[][] := ARRAY[
      ARRAY['6cb4378d-aa70-4add-bb61-1f2fdc86dfbb',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse11"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb1'],  -- 11
      ARRAY['cf72a231-b40f-4ffa-9834-bf4e40bf85ac',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse12"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb2'],  -- 12
      ARRAY['f63bbfbe-9926-48ae-8d04-421296a40634',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse13"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb3'],  -- 13
      ARRAY['8e3b28cc-74b1-4391-8359-c495538129b7',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse14"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdb4'],  -- 14
      ARRAY['e9e97450-ef9c-4f49-9375-adf11b6a158b',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse20"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc0'],  -- 20
      ARRAY['42dc1400-0a36-4c20-8742-115e22c42369',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse21"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc1'],  -- 21
      ARRAY['4540afba-ee51-42e4-bf74-d2346d813e89',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse22"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc2'],  -- 22
      ARRAY['757170c2-b2d5-4c71-85c1-61988b36e416',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse23"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdc3'],  -- 23
      ARRAY['757170c2-b2d5-4c71-85c1-61988b36e417',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse su-test-diff-data"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdf0'],  -- su-test-diff-data
      ARRAY['757170c2-b2d5-4c71-85c1-61988b36e418',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse su-test-diff-without-collected-data"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdf1'],  -- su-test-diff-without-collected-data
      ARRAY['27abfaed-187a-44ab-8287-af08f3bd7158',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse LOG2021X11Web-01"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdd1'],  -- LOG2021X11Web-01
      ARRAY['c118114a-c0be-462d-9fe9-604436bea20a',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse LOG2021X11Web-02"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdd2'],  -- LOG2021X11Web-02
      ARRAY['df044ba3-9abb-451e-9e4d-75ba98ace5e6',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse LOG2021X11Web-03"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbdd3'],  -- LOG2021X11Web-03
      ARRAY['fdc43238-cf8e-4a55-ad49-14ea8152728d',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse LOG2021X11Tel_01"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbde1'],  -- LOG2021X11Tel_01
      ARRAY['fa0a7a90-0324-429f-837b-ad06b79cfd7d',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse LOG2021X11Tel_02"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbde2'],  -- LOG2021X11Tel_02
      ARRAY['d51e29b9-b27b-4159-957c-6bb54d811a20',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse LOG2021X11Tel_03"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbde3'],  -- LOG2021X11Tel_03
      ARRAY['b78366f8-8653-448a-8754-53a3135a2130',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse1"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd01'], -- PROTO01
      ARRAY['b78366f8-8653-448a-8754-53a3135a2131',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse2"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd02'], -- PROTO02
      ARRAY['b78366f8-8653-448a-8754-53a3135a2132',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse3"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd03'], -- PROTO03
      ARRAY['b78366f8-8653-448a-8754-53a3135a2133',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse4"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd04'], -- PROTO04
      ARRAY['b78366f8-8653-448a-8754-53a3135a2134',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse5"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd05'], -- PROTO05
      ARRAY['b78366f8-8653-448a-8754-53a3135a2135',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse6"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd06'], -- PROTO06
      ARRAY['b78366f8-8653-448a-8754-53a3135a2136',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse7"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd07'], -- PROTO07
      ARRAY['b78366f8-8653-448a-8754-53a3135a2137',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse8"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'], -- PROTO08
      ARRAY['b78366f8-8653-448a-8754-53a3135a2138',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse9"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'], -- PROTO09
      ARRAY['b78366f8-8653-448a-8754-53a3135a2140',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse10"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'], -- PROTO10
      ARRAY['b78366f8-8653-448a-8754-53a3135a2141',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse11"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd11'], -- PROTO11
      ARRAY['b78366f8-8653-448a-8754-53a3135a2142',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse12"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd12'], -- PROTO12
      ARRAY['b78366f8-8653-448a-8754-53a3135a2143',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse13"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd13'], -- PROTO13
      ARRAY['b78366f8-8653-448a-8754-53a3135a2144',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse14"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd14'], -- PROTO14
      ARRAY['b78366f8-8653-448a-8754-53a3135a2145',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse15"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd15'], -- PROTO15
      ARRAY['b78366f8-8653-448a-8754-53a3135a2146',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse16"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd16'], -- PROTO16
      ARRAY['b78366f8-8653-448a-8754-53a3135a2147',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse17"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd17'], -- PROTO17
      ARRAY['b78366f8-8653-448a-8754-53a3135a2148',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse18"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd18'], -- PROTO18
      ARRAY['b78366f8-8653-448a-8754-53a3135a2149',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse19"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd19'], -- PROTO19
      ARRAY['b78366f8-8653-448a-8754-53a3135a2150',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse20"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd20'], -- PROTO20
      ARRAY['b78366f8-8653-448a-8754-53a3135a2151',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse21"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd21'], -- PROTO21
      ARRAY['b78366f8-8653-448a-8754-53a3135a2152',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse22"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd22'], -- PROTO22
      ARRAY['b78366f8-8653-448a-8754-53a3135a2153',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse23"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd23'], -- PROTO23
      ARRAY['b78366f8-8653-448a-8754-53a3135a2154',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse24"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd24'], -- PROTO24
      ARRAY['b78366f8-8653-448a-8754-53a3135a2155',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse25"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd25'], -- PROTO25
      ARRAY['b78366f8-8653-448a-8754-53a3135a2156',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse26"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd26'], -- PROTO26
      ARRAY['b78366f8-8653-448a-8754-53a3135a2157',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse27"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd27'], -- PROTO27
      ARRAY['b78366f8-8653-448a-8754-53a3135a2158',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse28"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd28'], -- PROTO28
      ARRAY['b78366f8-8653-448a-8754-53a3135a2159',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse29"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd29'], -- PROTO29
      ARRAY['b78366f8-8653-448a-8754-53a3135a2160',
            '{"EXTERNAL": {}, "CALCULATED": {}, "COLLECTED": {"TEXT": {"COLLECTED": "super réponse30"}}}',
            '0c83fb82-0197-7197-8e8c-a6ce2c2dbd30']  -- PROTO30
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
END $$;