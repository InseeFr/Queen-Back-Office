INSERT INTO questionnaire_model(id, label, model) VALUES('simpsons', 'Questionnaire about the Simpsons tv show', '{
  "id": "i6vwi0",
  "label": "Questionnaire SIMPSONS",
  "components": [
    {
      "id": "j6p0ti5h",
      "componentType": "Sequence",
      "label": "\"I - Introduction\"",
      "declarations": [
        {
          "id": "j6p0ti5h-d1",
          "declarationType": "COMMENT",
          "position": "AFTER_QUESTION_TEXT",
          "label": "\"We''re going to test your knowledge about the simpsons series.Welcome in the simspons world!\""
        }
      ],
      "conditionFilter": "\"normal\""
    },

    {
      "id": "j6p3dkx6",
      "componentType": "Textarea",
      "mandatory": false,
      "maxLength": 500,
      "label": "\"➡ 1. Before starting, do you have any comments about the Simpsons family?\"",
      "conditionFilter": "\"normal\"",
      "response": {
        "name": "COMMENT",
        "valueState": [
          { "valueType": "PREVIOUS", "value": null },

          { "valueType": "COLLECTED", "value": null },

          { "valueType": "FORCED", "value": null },

          { "valueType": "EDITED", "value": null },

          { "valueType": "INPUTED", "value": null }
        ]
      }
    },

    {
      "id": "j6p0np9q",
      "componentType": "CheckboxBoolean",
      "mandatory": false,
      "label": "\"➡ 2. Are you ready?\"",
      "conditionFilter": "\"normal\"",
      "response": {
        "name": "READY",
        "valueState": [
          { "valueType": "PREVIOUS", "value": null },

          { "valueType": "COLLECTED", "value": null },

          { "valueType": "FORCED", "value": null },

          { "valueType": "EDITED", "value": null },

          { "valueType": "INPUTED", "value": null }
        ]
      }
    },

    {
      "id": "j6p0s7o5",
      "componentType": "Subsequence",
      "label": "\"General knowledge of the series\"",
      "conditionFilter": "if ((not( cast(READY,integer) <> 1) )) then \"normal\" else \"hidden\""
    },

    {
      "id": "j3343qhx",
      "componentType": "Input",
      "mandatory": false,
      "maxLength": 30,
      "label": "\"➡ 3. Who is the producer?\"",
      "conditionFilter": "if ((not( cast(READY,integer) <> 1) )) then \"normal\" else \"hidden\"",
      "response": {
        "name": "PRODUCER",
        "valueState": [
          { "valueType": "PREVIOUS", "value": null },

          { "valueType": "COLLECTED", "value": null },

          { "valueType": "FORCED", "value": null },

          { "valueType": "EDITED", "value": null },

          { "valueType": "INPUTED", "value": null }
        ]
      }
    }
  ],
  "variables": [
    { "variableType": "EXTERNAL", "name": "LAST_BROADCAST", "value": null },

    { "variableType": "COLLECTED", "name": "COMMENT", "responseRef": "COMMENT" },

    { "variableType": "COLLECTED", "name": "READY", "responseRef": "READY" },

    { "variableType": "COLLECTED", "name": "PRODUCER", "responseRef": "PRODUCER" },

    { "variableType": "COLLECTED", "name": "SEASON_NUMBER", "responseRef": "SEASON_NUMBER" },

    { "variableType": "COLLECTED", "name": "DATEFIRST", "responseRef": "DATEFIRST" },

    { "variableType": "COLLECTED", "name": "AUDIENCE_SHARE", "responseRef": "AUDIENCE_SHARE" },

    { "variableType": "COLLECTED", "name": "CITY", "responseRef": "CITY" },

    { "variableType": "COLLECTED", "name": "MAYOR", "responseRef": "MAYOR" },

    { "variableType": "COLLECTED", "name": "STATE", "responseRef": "STATE" },

    { "variableType": "COLLECTED", "name": "PET1", "responseRef": "PET1" },

    { "variableType": "COLLECTED", "name": "PET2", "responseRef": "PET2" },

    { "variableType": "COLLECTED", "name": "PET3", "responseRef": "PET3" },

    { "variableType": "COLLECTED", "name": "PET4", "responseRef": "PET4" },

    { "variableType": "COLLECTED", "name": "ICE_FLAVOUR1", "responseRef": "ICE_FLAVOUR1" },

    { "variableType": "COLLECTED", "name": "ICE_FLAVOUR2", "responseRef": "ICE_FLAVOUR2" },

    { "variableType": "COLLECTED", "name": "ICE_FLAVOUR3", "responseRef": "ICE_FLAVOUR3" },

    { "variableType": "COLLECTED", "name": "ICE_FLAVOUR4", "responseRef": "ICE_FLAVOUR4" },

    {
      "variableType": "COLLECTED",
      "name": "NUCLEAR_CHARACTER1",
      "responseRef": "NUCLEAR_CHARACTER1"
    },

    {
      "variableType": "COLLECTED",
      "name": "NUCLEAR_CHARACTER2",
      "responseRef": "NUCLEAR_CHARACTER2"
    },

    {
      "variableType": "COLLECTED",
      "name": "NUCLEAR_CHARACTER3",
      "responseRef": "NUCLEAR_CHARACTER3"
    },

    {
      "variableType": "COLLECTED",
      "name": "NUCLEAR_CHARACTER4",
      "responseRef": "NUCLEAR_CHARACTER4"
    },

    { "variableType": "COLLECTED", "name": "BIRTH_CHARACTER1", "responseRef": "BIRTH_CHARACTER1" },

    { "variableType": "COLLECTED", "name": "BIRTH_CHARACTER2", "responseRef": "BIRTH_CHARACTER2" },

    { "variableType": "COLLECTED", "name": "BIRTH_CHARACTER3", "responseRef": "BIRTH_CHARACTER3" },

    { "variableType": "COLLECTED", "name": "BIRTH_CHARACTER4", "responseRef": "BIRTH_CHARACTER4" },

    { "variableType": "COLLECTED", "name": "BIRTH_CHARACTER5", "responseRef": "BIRTH_CHARACTER5" },

    {
      "variableType": "COLLECTED",
      "name": "PERCENTAGE_EXPENSES11",
      "responseRef": "PERCENTAGE_EXPENSES11"
    },

    {
      "variableType": "COLLECTED",
      "name": "PERCENTAGE_EXPENSES21",
      "responseRef": "PERCENTAGE_EXPENSES21"
    },

    {
      "variableType": "COLLECTED",
      "name": "PERCENTAGE_EXPENSES31",
      "responseRef": "PERCENTAGE_EXPENSES31"
    },

    {
      "variableType": "COLLECTED",
      "name": "PERCENTAGE_EXPENSES41",
      "responseRef": "PERCENTAGE_EXPENSES41"
    },

    {
      "variableType": "COLLECTED",
      "name": "PERCENTAGE_EXPENSES51",
      "responseRef": "PERCENTAGE_EXPENSES51"
    },

    {
      "variableType": "COLLECTED",
      "name": "PERCENTAGE_EXPENSES61",
      "responseRef": "PERCENTAGE_EXPENSES61"
    },

    {
      "variableType": "COLLECTED",
      "name": "PERCENTAGE_EXPENSES71",
      "responseRef": "PERCENTAGE_EXPENSES71"
    },

    { "variableType": "COLLECTED", "name": "CLOWNING11", "responseRef": "CLOWNING11" },

    { "variableType": "COLLECTED", "name": "CLOWNING12", "responseRef": "CLOWNING12" },

    { "variableType": "COLLECTED", "name": "CLOWNING21", "responseRef": "CLOWNING21" },

    { "variableType": "COLLECTED", "name": "CLOWNING22", "responseRef": "CLOWNING22" },

    { "variableType": "COLLECTED", "name": "CLOWNING31", "responseRef": "CLOWNING31" },

    { "variableType": "COLLECTED", "name": "CLOWNING32", "responseRef": "CLOWNING32" },

    { "variableType": "COLLECTED", "name": "CLOWNING41", "responseRef": "CLOWNING41" },

    { "variableType": "COLLECTED", "name": "CLOWNING42", "responseRef": "CLOWNING42" },

    { "variableType": "COLLECTED", "name": "TRAVEL11", "responseRef": "TRAVEL11" },

    { "variableType": "COLLECTED", "name": "TRAVEL12", "responseRef": "TRAVEL12" },

    { "variableType": "COLLECTED", "name": "TRAVEL13", "responseRef": "TRAVEL13" },

    { "variableType": "COLLECTED", "name": "TRAVEL14", "responseRef": "TRAVEL14" },

    { "variableType": "COLLECTED", "name": "TRAVEL15", "responseRef": "TRAVEL15" },

    { "variableType": "COLLECTED", "name": "TRAVEL16", "responseRef": "TRAVEL16" },

    { "variableType": "COLLECTED", "name": "TRAVEL21", "responseRef": "TRAVEL21" },

    { "variableType": "COLLECTED", "name": "TRAVEL22", "responseRef": "TRAVEL22" },

    { "variableType": "COLLECTED", "name": "TRAVEL23", "responseRef": "TRAVEL23" },

    { "variableType": "COLLECTED", "name": "TRAVEL24", "responseRef": "TRAVEL24" },

    { "variableType": "COLLECTED", "name": "TRAVEL25", "responseRef": "TRAVEL25" },

    { "variableType": "COLLECTED", "name": "TRAVEL26", "responseRef": "TRAVEL26" },

    { "variableType": "COLLECTED", "name": "TRAVEL31", "responseRef": "TRAVEL31" },

    { "variableType": "COLLECTED", "name": "TRAVEL32", "responseRef": "TRAVEL32" },

    { "variableType": "COLLECTED", "name": "TRAVEL33", "responseRef": "TRAVEL33" },

    { "variableType": "COLLECTED", "name": "TRAVEL34", "responseRef": "TRAVEL34" },

    { "variableType": "COLLECTED", "name": "TRAVEL35", "responseRef": "TRAVEL35" },

    { "variableType": "COLLECTED", "name": "TRAVEL36", "responseRef": "TRAVEL36" },

    { "variableType": "COLLECTED", "name": "TRAVEL41", "responseRef": "TRAVEL41" },

    { "variableType": "COLLECTED", "name": "TRAVEL42", "responseRef": "TRAVEL42" },

    { "variableType": "COLLECTED", "name": "TRAVEL43", "responseRef": "TRAVEL43" },

    { "variableType": "COLLECTED", "name": "TRAVEL44", "responseRef": "TRAVEL44" },

    { "variableType": "COLLECTED", "name": "TRAVEL45", "responseRef": "TRAVEL45" },

    { "variableType": "COLLECTED", "name": "TRAVEL46", "responseRef": "TRAVEL46" },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS11",
      "responseRef": "FAVOURITE_CHARACTERS11"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS12",
      "responseRef": "FAVOURITE_CHARACTERS12"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS21",
      "responseRef": "FAVOURITE_CHARACTERS21"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS22",
      "responseRef": "FAVOURITE_CHARACTERS22"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS31",
      "responseRef": "FAVOURITE_CHARACTERS31"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS32",
      "responseRef": "FAVOURITE_CHARACTERS32"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS41",
      "responseRef": "FAVOURITE_CHARACTERS41"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS42",
      "responseRef": "FAVOURITE_CHARACTERS42"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS51",
      "responseRef": "FAVOURITE_CHARACTERS51"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS52",
      "responseRef": "FAVOURITE_CHARACTERS52"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS61",
      "responseRef": "FAVOURITE_CHARACTERS61"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS62",
      "responseRef": "FAVOURITE_CHARACTERS62"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS71",
      "responseRef": "FAVOURITE_CHARACTERS71"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS72",
      "responseRef": "FAVOURITE_CHARACTERS72"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS81",
      "responseRef": "FAVOURITE_CHARACTERS81"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS82",
      "responseRef": "FAVOURITE_CHARACTERS82"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS91",
      "responseRef": "FAVOURITE_CHARACTERS91"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS92",
      "responseRef": "FAVOURITE_CHARACTERS92"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS101",
      "responseRef": "FAVOURITE_CHARACTERS101"
    },

    {
      "variableType": "COLLECTED",
      "name": "FAVOURITE_CHARACTERS102",
      "responseRef": "FAVOURITE_CHARACTERS102"
    },

    { "variableType": "COLLECTED", "name": "SURVEY_COMMENT", "responseRef": "SURVEY_COMMENT" },

    {
      "variableType": "CALCULATED",
      "name": "SUM_EXPENSES",
      "expression": "cast(PERCENTAGE_EXPENSES11,number) + cast(PERCENTAGE_EXPENSES21,number) + cast(PERCENTAGE_EXPENSES31,number)+ cast(PERCENTAGE_EXPENSES41,number)+ cast(PERCENTAGE_EXPENSES51,number)+ cast(PERCENTAGE_EXPENSES61,number)+ cast(PERCENTAGE_EXPENSES71,number)+ cast(PERCENTAGE_EXPENSES81,number)+ cast(PERCENTAGE_EXPENSES91,number)+ cast(PERCENTAGE_EXPENSES101,number)"
    }
  ]
}');