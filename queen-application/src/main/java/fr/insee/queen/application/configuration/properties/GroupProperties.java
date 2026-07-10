package fr.insee.queen.application.configuration.properties;

import fr.insee.queen.domain.group.model.GroupKind;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "application.group")
public record GroupProperties(
        @NotNull(message = "application.group.kind must be specified (CAMPAIGN | PARTITION)")
        GroupKind kind) {
}
