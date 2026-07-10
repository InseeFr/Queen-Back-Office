package fr.insee.queen.domain.group.service.dummy;

import fr.insee.queen.domain.group.service.NomenclatureService;
import fr.insee.queen.domain.group.model.Nomenclature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class NomenclatureFakeService implements NomenclatureService {
    @Setter
    private List<String> nonExistingNomenclatures = new ArrayList<>();

    @Getter
    private boolean saved = false;

    @Override
    public Nomenclature getNomenclature(String id) {
        return null;
    }

    @Override
    public boolean existsById(String id) {
        return !nonExistingNomenclatures.contains(id);
    }

    @Override
    public boolean areNomenclaturesValid(Set<String> nomenclatureIds) {
        return false;
    }

    @Override
    public void saveNomenclature(Nomenclature nomenclature) {
        saved = true;
    }

    @Override
    public List<String> getAllNomenclatureIds() {
        return null;
    }

    @Override
    public List<String> findRequiredNomenclatureByGroup(String groupId) {
        return null;
    }

    @Override
    public List<String> findRequiredNomenclatureByQuestionnaire(String questionnaireId) {
        return null;
    }
}
