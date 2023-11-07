package fr.insee.queen.api.controller.integration.component;

import fr.insee.queen.api.controller.integration.component.builder.CampaignBuilder;
import fr.insee.queen.api.controller.integration.component.builder.NomenclatureBuilder;
import fr.insee.queen.api.controller.integration.component.builder.QuestionnaireBuilder;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationComponentException;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@Slf4j
@AllArgsConstructor
@Transactional
public class IntegrationComponent {
    private final NomenclatureBuilder nomenclatureBuilder;
    private final CampaignBuilder campaignBuilder;
    private final QuestionnaireBuilder questionnaireBuilder;

    public IntegrationResultDto integrateContext(MultipartFile file) {
        try {
            File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
            return integrateContext(zip, file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    private IntegrationResultDto integrateContext(File zip, MultipartFile file) throws IOException {
        try (FileOutputStream o = new FileOutputStream(zip)) {
            IOUtils.copy(file.getInputStream(), o);
            return doIntegration(zip);
        } catch (ParserConfigurationException | SAXException | JSONException | XPathExpressionException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }

    private IntegrationResultDto doIntegration(File zip) throws ParserConfigurationException, SAXException, XPathExpressionException, JSONException {
        IntegrationResultDto result = new IntegrationResultDto();
        ZipEntry campaignXmlFile = null;
        ZipEntry nomenclaturesXmlFile =  null;
        ZipEntry questionnaireModelsXmlFile = null;
        HashMap<String, ZipEntry> nomenclatureJsonFiles = new HashMap<>();
        HashMap<String, ZipEntry> questionnaireModelJsonFiles = new HashMap<>();

        try(ZipFile zf = new ZipFile(zip)){
            String nomenclaturesPattern = "nomenclatures/.*json";
            String questionnairesPattern = "questionnaireModels/.*json";
            Enumeration<? extends ZipEntry> e = zf.entries();

            while(e.hasMoreElements()){
                ZipEntry entry = e.nextElement();
                switch(entry.getName()) {
                    case CampaignBuilder.CAMPAIGN_XML -> campaignXmlFile = entry;
                    case NomenclatureBuilder.NOMENCLATURES_XML -> nomenclaturesXmlFile = entry;
                    case QuestionnaireBuilder.QUESTIONNAIRE_MODELS_XML -> questionnaireModelsXmlFile = entry;
                    default -> {
                        if(Pattern.matches(nomenclaturesPattern,entry.getName())){
                            nomenclatureJsonFiles.put(entry.getName(), entry);

                        }
                        if(Pattern.matches(questionnairesPattern,entry.getName())){
                            questionnaireModelJsonFiles.put(entry.getName(), entry);
                        }
                    }
                }
            }

            List<IntegrationResultUnitDto> nomenclatureResults = nomenclatureBuilder.build(zf, nomenclaturesXmlFile, nomenclatureJsonFiles);
            result.nomenclatures(nomenclatureResults);

            Pair<Optional<String>, IntegrationResultUnitDto> campaignResult = campaignBuilder.build(zf, campaignXmlFile);
            result.campaign(campaignResult.getSecond());

            Optional<String> campaignId = campaignResult.getFirst();
            if(campaignId.isEmpty()) {
                return result;
            }

            List<IntegrationResultUnitDto> questionnaireResults = questionnaireBuilder.build(campaignId.get(), zf, questionnaireModelsXmlFile, questionnaireModelJsonFiles);
            result.questionnaireModels(questionnaireResults);

            return result;
        }
        catch(IOException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationComponentException(e.getMessage());
        }
    }
}

