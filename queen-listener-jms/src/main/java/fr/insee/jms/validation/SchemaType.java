package fr.insee.jms.validation;

import lombok.Getter;

@Getter
public enum SchemaType {
    PROCESS_MESSAGE(Names.PROCESS_MESSAGE),
    INTERROGATION(Names.INTERROGATION);

    public static class Names {
        public static final String PROCESS_MESSAGE = "/modele-filiere-spec/CommandRequest.json";
        public static final String INTERROGATION = "/modele-filiere-spec/Interrogation.json";
        private Names() {

        }
    }
    private final String schemaFileName;

    SchemaType(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }
}
