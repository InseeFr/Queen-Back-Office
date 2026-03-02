package fr.insee.queen.infrastructure.broker.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Objects;

/**
 * EventPayloadLeafStatesInnerDto
 */

@JsonTypeName("Event_payload_leafStates_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-24T14:11:23.623660900+01:00[Europe/Paris]", comments = "Generator version: 7.9.0")
public class EventPayloadLeafStatesInnerDto {

    /**
     * Gets or Sets state
     */
    public enum StateEnum {
        INIT("INIT"),

        COMPLETED("COMPLETED");

        private String value;

        StateEnum(String value) {
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
        public static StateEnum fromValue(String value) {
            for (StateEnum b : StateEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            return null;
        }
    }

    private StateEnum state = null;

    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, timezone = "UTC")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant date;

    public EventPayloadLeafStatesInnerDto() {
        super();
    }

    /**
     * Constructor with only required parameters
     */
    public EventPayloadLeafStatesInnerDto(Instant date) {
        this.date = date;
    }

    public EventPayloadLeafStatesInnerDto state(StateEnum state) {
        this.state = state;
        return this;
    }

    /**
     * Get state
     * @return state
     */

    @JsonProperty("state")
    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public EventPayloadLeafStatesInnerDto date(Instant date) {
        this.date = date;
        return this;
    }

    /**
     * Get date
     * @return date
     */
    @NotNull @Valid
    @JsonProperty("date")
    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventPayloadLeafStatesInnerDto eventPayloadLeafStatesInner = (EventPayloadLeafStatesInnerDto) o;
        return Objects.equals(this.state, eventPayloadLeafStatesInner.state) &&
                Objects.equals(this.date, eventPayloadLeafStatesInner.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, date);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EventPayloadLeafStatesInnerDto {\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    date: ").append(toIndentedString(date)).append("\n");
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

        private EventPayloadLeafStatesInnerDto instance;

        public Builder() {
            this(new EventPayloadLeafStatesInnerDto());
        }

        protected Builder(EventPayloadLeafStatesInnerDto instance) {
            this.instance = instance;
        }

        protected Builder copyOf(EventPayloadLeafStatesInnerDto value) {
            this.instance.setState(value.state);
            this.instance.setDate(value.date);
            return this;
        }

        public Builder state(StateEnum state) {
            this.instance.state(state);
            return this;
        }

        public Builder date(Instant date) {
            this.instance.date(date);
            return this;
        }

        /**
         * returns a built EventPayloadLeafStatesInnerDto instance.
         *
         * The builder is not reusable (NullPointerException)
         */
        public EventPayloadLeafStatesInnerDto build() {
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
