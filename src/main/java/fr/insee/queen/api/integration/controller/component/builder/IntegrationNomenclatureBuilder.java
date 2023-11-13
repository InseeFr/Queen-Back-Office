package fr.insee.queen.api.integration.controller.component.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.api.integration.controller.component.builder.schema.SchemaComponent;
import fr.insee.queen.api.integration.controller.component.exception.IntegrationValidationException;
import fr.insee.queen.api.integration.controller.dto.input.NomenclatureIntegrationData;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.api.integration.service.IntegrationService;
import fr.insee.queen.api.integration.service.model.IntegrationResult;
import fr.insee.queen.api.integration.service.model.IntegrationResultLabel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handle the integration of nomenclatures
 */
@Component
@Slf4j
@AllArgsConstructor
public class IntegrationNomenclatureBuilder implements NomenclatureBuilder {
    private final SchemaComponent schemaComponent;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final IntegrationService integrationService;
    private static final String LABEL = "Label";
    private static final String ID = "Id";
    private static final String FILENAME = "FileName";
    public static final String NOMENCLATURES_XML = "nomenclatures.xml";

    @Override
    public List<IntegrationResultUnitDto> build(ZipFile integrationZipFile) {
        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(integrationZipFile, NOMENCLATURES_XML, "nomenclatures_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            return List.of(ex.resultError());
        }
        return buildNomenclatures(integrationZipFile);
    }

    private List<IntegrationResultUnitDto> buildNomenclatures(ZipFile zf) {

        List<IntegrationResultUnitDto> results = new ArrayList<>();

        Document doc;
        try {
            doc = schemaComponent.buildDocument(zf.getInputStream(zf.getEntry(NOMENCLATURES_XML)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return List.of(IntegrationResultUnitDto.integrationResultUnitError(null, e.getMessage()));
        }

        NodeList nomenclatureNodes = doc.getElementsByTagName("Nomenclatures").item(0).getChildNodes();
        for (int i = 0; i < nomenclatureNodes.getLength(); i++) {
            if (nomenclatureNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element nomenclatureElement = (Element) nomenclatureNodes.item(i);
            try {
                String nomenclatureId = nomenclatureElement.getElementsByTagName(ID).item(0).getTextContent();
                String nomenclatureLabel = nomenclatureElement.getElementsByTagName(LABEL).item(0).getTextContent();
                String nomenclatureFilename = nomenclatureElement.getElementsByTagName(FILENAME).item(0).getTextContent();
                ArrayNode nomenclatureValue = readNomenclatureStream(nomenclatureId, nomenclatureFilename, zf);
                results.add(buildNomenclature(nomenclatureId, nomenclatureLabel, nomenclatureValue));
            } catch (IntegrationValidationException ex) {
                results.add(ex.resultError());
            }
        }
        return results;
    }

    private IntegrationResultUnitDto buildNomenclature(String nomenclatureId, String nomenclatureLabel, ArrayNode nomenclatureValue) {
        NomenclatureIntegrationData nomenclature = new NomenclatureIntegrationData(nomenclatureId, nomenclatureLabel, nomenclatureValue);
        Set<ConstraintViolation<NomenclatureIntegrationData>> violations = validator.validate(nomenclature);
        if (!violations.isEmpty()) {
            StringBuilder violationMessage = new StringBuilder();
            for (ConstraintViolation<NomenclatureIntegrationData> violation : violations) {
                violationMessage
                        .append(violation.getPropertyPath().toString())
                        .append(": ")
                        .append(violation.getMessage())
                        .append(". ");
            }
            return IntegrationResultUnitDto.integrationResultUnitError(nomenclatureId, violationMessage.toString());
        }
        IntegrationResult result = integrationService.create(NomenclatureIntegrationData.toModel(nomenclature));
        return IntegrationResultUnitDto.fromModel(result);
    }

    private ArrayNode readNomenclatureStream(String nomenclatureId, String nomenclatureFilename, ZipFile zipFile) throws IntegrationValidationException {
        try {
            InputStream questionnaireInputStream = getNomenclatureInputStream(zipFile, nomenclatureId, nomenclatureFilename);
            return objectMapper.readValue(questionnaireInputStream, ArrayNode.class);
        } catch (IOException e) {
            log.info("Could not parse json in file {}", nomenclatureFilename);
            throw new IntegrationValidationException(IntegrationResultUnitDto.integrationResultUnitError(
                    nomenclatureId,
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, nomenclatureFilename))
            );
        }
    }

    private InputStream getNomenclatureInputStream(ZipFile zf, String nomenclatureId, String nomenclatureFilename) throws IntegrationValidationException, IOException {
        ZipEntry nomenclatureValueEntry = zf.getEntry("nomenclatures/" + nomenclatureFilename);
        if (nomenclatureValueEntry == null) {
            log.info("Nomenclature file {} could not be found in input zip", nomenclatureFilename);

            throw new IntegrationValidationException(IntegrationResultUnitDto.integrationResultUnitError(
                    nomenclatureId,
                    String.format(IntegrationResultLabel.NOMENCLATURE_FILE_NOT_FOUND, nomenclatureFilename))
            );
        }
        return zf.getInputStream(nomenclatureValueEntry);
    }
}

