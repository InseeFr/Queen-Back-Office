package fr.insee.queen.api.service.pilotage;

public enum PilotageRole {
    INTERVIEWER( ""),
    REVIEWER("reviewer");

    private final String expectedRole;

    PilotageRole(String expectedRole) {
        this.expectedRole = expectedRole;
    }

    public String getExpectedRole() {
        return expectedRole;
    }
}
