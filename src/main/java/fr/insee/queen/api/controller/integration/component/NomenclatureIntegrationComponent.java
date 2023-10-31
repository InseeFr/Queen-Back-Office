package fr.insee.queen.api.controller.integration.component;

import fr.insee.queen.api.controller.integration.component.builder.NomenclatureBuilder;
import fr.insee.queen.api.controller.integration.component.creator.NomenclatureCreator;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationsException;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@AllArgsConstructor
@Slf4j
public class NomenclatureIntegrationComponent {
    private final NomenclatureBuilder nomenclatureBuilder;
    private final NomenclatureCreator nomenclatureCreator;

    public List<IntegrationResultUnitDto> process(ZipEntry nomenclaturesXmlFile, HashMap<String, ZipEntry> nomenclatureJsonFiles, ZipFile zf) throws ParserConfigurationException, IOException, SAXException {
        try {
            List<NomenclatureInputDto> nomenclatures = nomenclatureBuilder.build(zf, nomenclaturesXmlFile, nomenclatureJsonFiles);
            return nomenclatureCreator.create(nomenclatures);
        } catch (IntegrationValidationsException e) {
            return e.resultErrors().stream()
                    .map(IntegrationResultUnitDto.class::cast)
                    .toList();
        }
    }
}
