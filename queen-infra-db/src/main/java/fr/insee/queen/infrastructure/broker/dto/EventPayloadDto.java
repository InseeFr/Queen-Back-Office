package fr.insee.queen.infrastructure.broker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Content specific to the event type
 */

@JsonTypeName("Event_payload")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-24T14:11:23.623660900+01:00[Europe/Paris]", comments = "Generator version: 7.9.0")
public class EventPayloadDto {

    private String interrogationId;

    private ModeDto mode;

    @Valid
    private List<@Valid EventPayloadLeafStatesInnerDto> leafStates = new ArrayList<>();

    public EventPayloadDto() {
        super();
    }

    /**
     * Constructor with only required parameters
     */
    public EventPayloadDto(String interrogationId, ModeDto mode) {
        this.interrogationId = interrogationId;
        this.mode = mode;
    }

    public EventPayloadDto interrogationId(String interrogationId) {
        this.interrogationId = interrogationId;
        return this;
    }

    /**
     * Identifier of the survey unit or interrogation
     * @return interrogationId
     */
    @NotNull
    @JsonProperty("interrogationId")
    public String getInterrogationId() {
        return interrogationId;
    }

    public void setInterrogationId(String interrogationId) {
        this.interrogationId = interrogationId;
    }

    public EventPayloadDto mode(ModeDto mode) {
        this.mode = mode;
        return this;
    }

    /**
     * Get mode
     * @return mode
     */
    @NotNull @Valid
    @JsonProperty("mode")
    public ModeDto getMode() {
        return mode;
    }

    public void setMode(ModeDto mode) {
        this.mode = mode;
    }

    public EventPayloadDto leafStates(List<@Valid EventPayloadLeafStatesInnerDto> leafStates) {
        this.leafStates = leafStates;
        return this;
    }

    public EventPayloadDto addLeafStatesItem(EventPayloadLeafStatesInnerDto leafStatesItem) {
        if (this.leafStates == null) {
            this.leafStates = new ArrayList<>();
        }
        this.leafStates.add(leafStatesItem);
        return this;
    }

    /**
     * Leaf states (if applicable)
     * @return leafStates
     */
    @Valid
    @JsonProperty("leafStates")
    public List<@Valid EventPayloadLeafStatesInnerDto> getLeafStates() {
        return leafStates;
    }

    public void setLeafStates(List<@Valid EventPayloadLeafStatesInnerDto> leafStates) {
        this.leafStates = leafStates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventPayloadDto eventPayload = (EventPayloadDto) o;
        return Objects.equals(this.interrogationId, eventPayload.interrogationId) &&
                Objects.equals(this.mode, eventPayload.mode) &&
                Objects.equals(this.leafStates, eventPayload.leafStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interrogationId, mode, leafStates);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EventPayloadDto {\n");
        sb.append("    interrogationId: ").append(toIndentedString(interrogationId)).append("\n");
        sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
        sb.append("    leafStates: ").append(toIndentedString(leafStates)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public static class Builder {

        private EventPayloadDto instance;

        public Builder() {
            this(new EventPayloadDto());
        }

        protected Builder(EventPayloadDto instance) {
            this.instance = instance;
        }

        protected Builder copyOf(EventPayloadDto value) {
            this.instance.setInterrogationId(value.interrogationId);
            this.instance.setMode(value.mode);
            this.instance.setLeafStates(value.leafStates);
            return this;
        }

        public Builder interrogationId(String interrogationId) {
            this.instance.interrogationId(interrogationId);
            return this;
        }

        public Builder mode(ModeDto mode) {
            this.instance.mode(mode);
            return this;
        }

        public Builder leafStates(List<@Valid EventPayloadLeafStatesInnerDto> leafStates) {
            this.instance.leafStates(leafStates);
            return this;
        }

        /**
         * returns a built EventPayloadDto instance.
         *
         * The builder is not reusable (NullPointerException)
         */
        public EventPayloadDto build() {
            try {
                return this.instance;
            } finally {
                // ensure that this.instance is not reused
                this.instance = null;
            }
        }

        @Override
        public String toString() {
            return getClass() + "=(" + instance + ")";
        }
    }

    /**
     * Create a builder with no initialized field (except for the default values).
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create a builder with a shallow copy of this instance.
     */
    public Builder toBuilder() {
        Builder builder = new Builder();
        return builder.copyOf(this);
    }

}


