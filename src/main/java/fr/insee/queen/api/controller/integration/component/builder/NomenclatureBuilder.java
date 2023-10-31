package fr.insee.queen.api.controller.integration.component.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.api.controller.integration.component.IntegrationResultLabel;
import fr.insee.queen.api.controller.integration.component.SchemaComponent;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationsException;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@Slf4j
@AllArgsConstructor
public class NomenclatureBuilder {
    private final SchemaComponent schemaComponent;
    private final Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String LABEL = "Label";
    private static final String ID = "Id";
    private static final String FILENAME = "FileName";
    public static final String NOMENCLATURES_XML = "nomenclatures.xml";

    public List<NomenclatureInputDto> build(ZipFile zf, ZipEntry nomenclaturesXmlFile,
                                            HashMap<String, ZipEntry> nomenclatureJsonFiles) throws IntegrationValidationsException, ParserConfigurationException, IOException, SAXException {
        if(nomenclaturesXmlFile == null) {
            throw new IntegrationValidationsException(List.of(new IntegrationResultErrorUnitDto(
                    NOMENCLATURES_XML,
                    String.format(IntegrationResultLabel.FILE_NOT_FOUND, NOMENCLATURES_XML))));
        }

        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(zf, nomenclaturesXmlFile, "nomenclatures_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            throw new IntegrationValidationsException(List.of(ex.resultError()));
        }
        return buildNomenclatures(zf, nomenclaturesXmlFile, nomenclatureJsonFiles);
    }

    private List<NomenclatureInputDto> buildNomenclatures(ZipFile zf, ZipEntry nomenclaturesXmlFile,
                                                          HashMap<String, ZipEntry> nomenclatureJsonFiles) throws ParserConfigurationException, SAXException, IOException, IntegrationValidationsException {

        List<NomenclatureInputDto> nomenclatures = new ArrayList<>();
        List<IntegrationResultErrorUnitDto> resultErrors = new ArrayList<>();

        Document doc = schemaComponent.buildDocument(zf.getInputStream(nomenclaturesXmlFile));
        NodeList nomenclatureNodes = doc.getElementsByTagName("Nomenclatures").item(0).getChildNodes();
        for (int i = 0; i < nomenclatureNodes.getLength(); i++) {
            if(nomenclatureNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element nomenclatureElement = (Element) nomenclatureNodes.item(i);
            try {
                String nomenclatureId = nomenclatureElement.getElementsByTagName(ID).item(0).getTextContent();
                String nomenclatureLabel = nomenclatureElement.getElementsByTagName(LABEL).item(0).getTextContent();
                String nomenclatureFilename = nomenclatureElement.getElementsByTagName(FILENAME).item(0).getTextContent();
                ArrayNode nomenclatureValue = readNomenclatureStream(nomenclatureId, nomenclatureFilename, zf, nomenclatureJsonFiles);
                NomenclatureInputDto nomenclature = buildNomenclature(nomenclatureId, nomenclatureLabel, nomenclatureValue);
                nomenclatures.add(nomenclature);
            } catch (IntegrationValidationException ex) {
                resultErrors.add(ex.resultError());
            }

        }

        if(!resultErrors.isEmpty()) {
            throw new IntegrationValidationsException(resultErrors);
        }
        return nomenclatures;
    }

    private NomenclatureInputDto buildNomenclature(String nomenclatureId, String nomenclatureLabel, ArrayNode nomenclatureValue) throws IntegrationValidationException {

        NomenclatureInputDto nomenclatureInput = new NomenclatureInputDto(nomenclatureId, nomenclatureLabel, nomenclatureValue);
        Set<ConstraintViolation<NomenclatureInputDto>> violations = validator.validate(nomenclatureInput);
        if (violations.isEmpty()) {
            return nomenclatureInput;
        }

        StringBuilder violationMessage = new StringBuilder();
        for(ConstraintViolation<NomenclatureInputDto> violation : violations) {
            violationMessage
                    .append(violation.getPropertyPath().toString())
                    .append(": ")
                    .append(violation.getMessage())
                    .append(". ");
        }
        throw new IntegrationValidationException(
                new IntegrationResultErrorUnitDto(nomenclatureId, violationMessage.toString())
        );
    }

    private ArrayNode readNomenclatureStream(String nomenclatureId, String nomenclatureFilename, ZipFile zipFile, HashMap<String, ZipEntry> nomenclatureJsonFiles) throws IntegrationValidationException {
        try {
            InputStream questionnaireInputStream = getNomenclatureInputStream(zipFile, nomenclatureId, nomenclatureFilename, nomenclatureJsonFiles);
            return objectMapper.readValue(questionnaireInputStream, ArrayNode.class);
        } catch (IOException e) {
            log.info("Could not parse json in file {}", nomenclatureFilename);
            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    nomenclatureId,
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, nomenclatureFilename))
            );
        }
    }

    private InputStream getNomenclatureInputStream(ZipFile zf, String nomenclatureId, String nomenclatureFilename, HashMap<String, ZipEntry> nomenclatureJsonFiles) throws IntegrationValidationException, IOException {
        ZipEntry nomenclatureValueEntry = nomenclatureJsonFiles.get("nomenclatures/" +nomenclatureFilename);
        if(nomenclatureValueEntry == null) {
            log.info("Nomenclature file {} could not be found in input zip", nomenclatureFilename );

            throw new IntegrationValidationException(new IntegrationResultErrorUnitDto(
                    nomenclatureId,
                    String.format(IntegrationResultLabel.NOMENCLATURE_FILE_NOT_FOUND, nomenclatureFilename))
            );
        }
        return zf.getInputStream(nomenclatureValueEntry);
    }
}

