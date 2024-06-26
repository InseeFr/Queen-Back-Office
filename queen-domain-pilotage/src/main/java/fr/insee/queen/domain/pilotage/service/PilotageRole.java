package fr.insee.queen.domain.pilotage.service;

public enum PilotageRole {
    INTERVIEWER(""),
    REVIEWER("reviewer");

    private final String expectedRole;

    PilotageRole(String expectedRole) {
        this.expectedRole = expectedRole;
    }

    public String getExpectedRole() {
        return expectedRole;
    }
}
