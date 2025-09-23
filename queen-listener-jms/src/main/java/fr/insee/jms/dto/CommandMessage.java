package fr.insee.jms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(value = { "_id" })
public record CommandMessage(

    @NotNull
    @JsonProperty("processInstanceID")
    UUID processInstanceID,

    @NotNull
    @JsonProperty("inProgress")
    Boolean inProgress,

    // Chaîne JSON stringifiée (aucune validation de contenu)
    @NotNull
    @JsonProperty("payload")
    String payload,

    @NotNull
    @JsonProperty("CampaignID")
    String campaignID,

    @NotNull
    @JsonProperty("correlationID")
    UUID correlationID,

    @NotNull
    @JsonProperty("questionnaireID")
    String questionnaireID,

    @NotNull
    @JsonProperty("done")
    Boolean done,

    @NotNull
    @JsonProperty("dateCreation")
    DateCreation dateCreation,

    @NotNull
    @JsonProperty("replyTo")
    String replyTo
) {
    public record DateCreation(
        @NotNull
        @JsonProperty("$date")
        @JsonFormat(shape = JsonFormat.Shape.STRING) // force une string ISO-8601
        Instant date
    ) {}
}
