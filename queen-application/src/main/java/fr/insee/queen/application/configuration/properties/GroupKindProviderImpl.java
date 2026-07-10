package fr.insee.queen.application.configuration.properties;

import fr.insee.queen.domain.group.gateway.GroupKindProvider;
import fr.insee.queen.domain.group.model.GroupKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupKindProviderImpl implements GroupKindProvider {
    private final GroupProperties groupProperties;

    @Override
    public GroupKind getKind() {
        return groupProperties.kind();
    }
}
