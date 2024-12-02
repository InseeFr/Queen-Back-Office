package fr.insee.queen.infrastructure.mongo.configuration.migration.changelog;

import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import io.mongock.api.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@Slf4j
@ChangeUnit(order = "001", id = "initQuestionnaireModel", author = "davdarras")
public class InitQuestionnaireModelChangeLog {
    private static final String QUESTIONNAIRE_MODEL_COLLECTION = "questionnaire-model";

    @BeforeExecution
    public void createQuestionnaireCollection(MongoTemplate db) {
        String questionnaireModelSchema = """
        {
          "bsonType": "object",
          "title": "questionnaire-model-document",
          "required": ["_id", "data"],
          "properties": {
            "_id": { "bsonType": "string" },
            "label": { "bsonType": "string" },
            "campaign": {
              "bsonType": "object",
              "title": "campaign-object",
              "required": ["_id", "label"],
              "properties": {
                "_id": { "bsonType": "string" },
                "label": { "bsonType": "string" },
                "metadata": {
                  "bsonType": "object",
                  "properties": {
                    "inseeContext": {
                      "bsonType": "string",
                      "enum": ["household", "business"]
                    },
                    "variables": {
                      "bsonType": "array",
                      "items": {
                        "bsonType": "object",
                        "required": ["name", "value"],
                        "properties": {
                          "name": { "bsonType": "string" },
                          "value": { "bsonType": ["string", "bool", "double", "int", "long"] }
                        },
                        "additionalProperties": false
                      },
                      "minItems": 0
                    }
                  },
                  "additionalProperties": true
                }
              }
            },
            "value": {
              "bsonType": "object",
              "title": "questionnaire-model-value",
              "properties": {
                "data": {
                  "bsonType": "object",
                  "additionalProperties": true,
                }
              }
            },
            "nomenclatures": {
              "bsonType": "array",
              "items": {
                "bsonType": "string",
              }
            }
          }
        }
        """;

        Document schemaDocument = Document.parse(questionnaireModelSchema);
        MongoJsonSchema schema = MongoJsonSchema.of(schemaDocument);

        CollectionOptions.ValidationOptions validationOptions = new CollectionOptions.ValidationOptions(
                Validator.schema(schema), ValidationLevel.MODERATE, ValidationAction.ERROR
        );
        CollectionOptions createOptions = CollectionOptions.empty();
        createOptions = createOptions.validation(validationOptions);
        db.createCollection(QUESTIONNAIRE_MODEL_COLLECTION, createOptions);
    }

    @RollbackBeforeExecution
    public void executeBeforeRollback(MongoTemplate db) {
        db.dropCollection(QUESTIONNAIRE_MODEL_COLLECTION);
    }

    @Execution
    public void execute(MongoTemplate db) {
        // does not have an execution
    }

    @RollbackExecution
    public void executeRollback(MongoTemplate db) {
        // does not have an execution
    }
}

