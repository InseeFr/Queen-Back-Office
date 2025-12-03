package fr.insee.queen.infrastructure.broker.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * multimode Event
 */

@JsonTypeName("Event")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-24T14:11:23.623660900+01:00[Europe/Paris]", comments = "Generator version: 7.9.0")
public class EventDto {

    /**
     * Gets or Sets eventType
     */
    public enum EventTypeEnum {
        MULTIMODE_MOVED("MULTIMODE_MOVED"),

        QUESTIONNAIRE_INIT("QUESTIONNAIRE_INIT"),

        QUESTIONNAIRE_LEAF_STATES_UPDATED("QUESTIONNAIRE_LEAF_STATES_UPDATED"),

        QUESTIONNAIRE_COMPLETED("QUESTIONNAIRE_COMPLETED"),

        QUESTIONNAIRE_VALIDATED("QUESTIONNAIRE_VALIDATED");

        private String value;

        EventTypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static EventTypeEnum fromValue(String value) {
            for (EventTypeEnum b : EventTypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    private EventTypeEnum eventType;

    /**
     * Gets or Sets aggregateType
     */
    public enum AggregateTypeEnum {
        QUESTIONNAIRE("QUESTIONNAIRE");

        private String value;

        AggregateTypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator

        public static AggregateTypeEnum fromValue(String value) {
            for (AggregateTypeEnum b : AggregateTypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    private AggregateTypeEnum aggregateType;

    private EventPayloadDto payload;

    public EventDto() {
        super();
    }

    /**
     * Constructor with only required parameters
     */
    public EventDto(EventTypeEnum eventType, AggregateTypeEnum aggregateType, EventPayloadDto payload) {
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.payload = payload;
    }

    public EventDto eventType(EventTypeEnum eventType) {
        this.eventType = eventType;
        return this;
    }

    /**
     * Get eventType
     * @return eventType
     */
    @NotNull
    @JsonProperty("eventType")
    public EventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public EventDto aggregateType(AggregateTypeEnum aggregateType) {
        this.aggregateType = aggregateType;
        return this;
    }

    /**
     * Get aggregateType
     * @return aggregateType
     */
    @NotNull
    @JsonProperty("aggregateType")
    public AggregateTypeEnum getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(AggregateTypeEnum aggregateType) {
        this.aggregateType = aggregateType;
    }

    public EventDto payload(EventPayloadDto payload) {
        this.payload = payload;
        return this;
    }

    /**
     * Get payload
     * @return payload
     */
    @NotNull @Valid
    @JsonProperty("payload")
    public EventPayloadDto getPayload() {
        return payload;
    }

    public void setPayload(EventPayloadDto payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventDto event = (EventDto) o;
        return Objects.equals(this.eventType, event.eventType) &&
                Objects.equals(this.aggregateType, event.aggregateType) &&
                Objects.equals(this.payload, event.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, aggregateType, payload);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EventDto {\n");
        sb.append("    eventType: ").append(toIndentedString(eventType)).append("\n");
        sb.append("    aggregateType: ").append(toIndentedString(aggregateType)).append("\n");
        sb.append("    payload: ").append(toIndentedString(payload)).append("\n");
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

        private EventDto instance;

        public Builder() {
            this(new EventDto());
        }

        protected Builder(EventDto instance) {
            this.instance = instance;
        }

        protected Builder copyOf(EventDto value) {
            this.instance.setEventType(value.eventType);
            this.instance.setAggregateType(value.aggregateType);
            this.instance.setPayload(value.payload);
            return this;
        }

        public Builder eventType(EventTypeEnum eventType) {
            this.instance.eventType(eventType);
            return this;
        }

        public Builder aggregateType(AggregateTypeEnum aggregateType) {
            this.instance.aggregateType(aggregateType);
            return this;
        }

        public Builder payload(EventPayloadDto payload) {
            this.instance.payload(payload);
            return this;
        }

        /**
         * returns a built EventDto instance.
         *
         * The builder is not reusable (NullPointerException)
         */
        public EventDto build() {
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
