package fr.insee.queen.application.web.validation.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.*;
import fr.insee.queen.application.web.validation.exception.JsonValidatorComponentInitializationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class JsonValidatorComponent {
    private static final String SCHEMA_PREFIX_URI = "https://insee.fr/";
    private final JsonSchemaFactory factory;
    private final Map<SchemaType, URI> schemaUris;

    public JsonValidatorComponent() {
        schemaUris = new EnumMap<>(SchemaType.class);
        Map<String, String> schemas = new HashMap<>();
        ClassLoader classLoader = this.getClass().getClassLoader();

        for(SchemaType schemaType : SchemaType.values()) {
            try(InputStream schemaStream = classLoader.getResourceAsStream( "static/v3/" + schemaType.getSchemaFileName())) {
                assert schemaStream != null;
                String schemaData = new String(schemaStream.readAllBytes(), StandardCharsets.UTF_8);
                URI schemaUri = new URI(SCHEMA_PREFIX_URI + schemaType.getSchemaFileName());
                schemaUris.put(schemaType, schemaUri);
                schemas.put(schemaUri.toString(), schemaData);
            } catch(IOException | URISyntaxException e) {
                throw new JsonValidatorComponentInitializationException(e.getMessage());
            }
        }

        this.factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012,
                builder -> builder.schemaLoaders(schemaLoader -> schemaLoader.schemas(schemas)));
    }

    public Set<ValidationMessage> validate(SchemaType schemaType, JsonNode value) {
        JsonSchema schema = factory.getSchema(schemaUris.get(schemaType));
        return schema.validate(value);
    }
}
