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
@ChangeUnit(order = "003", id = "initSurveyUnit", author = "davdarras")
public class InitSurveyUnitChangeLog {
    private static final String SURVEY_UNIT_COLLECTION = "survey-unit";

    @BeforeExecution
    public void createSurveyUnitCollection(MongoTemplate db) {
        String surveyUnitSchema = """
        {
          "bsonType": "object",
          "required": ["_id", "campaign-id", "questionnaire-id"],
          "properties": {
            "_id": { "bsonType": "string" },
            "campaign-id": { "bsonType": "string" },
            "questionnaire-id": { "bsonType": "string" },
            "data": { "bsonType": "object" },
            "state-data": {
                "bsonType": "object",
                "properties": {
                    "date": { "bsonType": "long" },
                    "state": {
                        "bsonType": "string",
                        "enum": ["INIT", "COMPLETED", "VALIDATED", "TOEXTRACT", "EXTRACTED"]
                    },
                    "current-page": { "bsonType": "string" }
                }
            },
            "personalization": {
              "bsonType": "array",
              "items": {
                "bsonType": "object",
                "required": ["name", "value"],
                "properties": {
                  "name": { "bsonType": "string" },
                  "value": { "bsonType": "string" }
                },
                "additionalProperties": false
              }
            },
            "comment": { "bsonType": "object" }
          }
        }
        """;

        Document schemaDocument = Document.parse(surveyUnitSchema);
        MongoJsonSchema schema = MongoJsonSchema.of(schemaDocument);

        CollectionOptions.ValidationOptions validationOptions = new CollectionOptions.ValidationOptions(
                Validator.schema(schema), ValidationLevel.MODERATE, ValidationAction.ERROR
        );
        CollectionOptions createOptions = CollectionOptions.empty();
        createOptions = createOptions.validation(validationOptions);
        db.createCollection(SURVEY_UNIT_COLLECTION, createOptions);
    }

    @RollbackBeforeExecution
    public void executeBeforeRollback(MongoTemplate db) {
        db.dropCollection(SURVEY_UNIT_COLLECTION);
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


