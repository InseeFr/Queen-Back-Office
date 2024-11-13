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
@ChangeUnit(order = "002", id = "initNomenclature", author = "davdarras")
public class InitNomenclatureChangeLog {
    private static final String NOMENCLATURE_COLLECTION = "nomenclature";

    @BeforeExecution
    public void createNomenclatureCollection(MongoTemplate db) {
        String nomenclatureSchema = """
        {
          "bsonType": "object",
          "required": ["_id", "label"],
          "properties": {
            "_id": { "bsonType": "string" },
            "label": { "bsonType": "string" },
            "value": {
              "bsonType": "array",
              "items": {
                  "bsonType": "object",
                  "required": ["id", "label"],
                  "properties": {
                    "id": {
                      "bsonType": "string"
                    },
                    "label": {
                      "bsonType": "string"
                    }
                  },
                  "additionalProperties": true
              },
            }
          }
        }
        """;

        Document schemaDocument = Document.parse(nomenclatureSchema);
        MongoJsonSchema schema = MongoJsonSchema.of(schemaDocument);

        CollectionOptions.ValidationOptions validationOptions = new CollectionOptions.ValidationOptions(
                Validator.schema(schema), ValidationLevel.MODERATE, ValidationAction.ERROR
        );
        CollectionOptions createOptions = CollectionOptions.empty();
        createOptions = createOptions.validation(validationOptions);
        db.createCollection(NOMENCLATURE_COLLECTION, createOptions);
    }

    @RollbackBeforeExecution
    public void executeBeforeRollback(MongoTemplate db) {
        db.dropCollection(NOMENCLATURE_COLLECTION);
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

