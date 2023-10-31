package fr.insee.queen.api.controller.integration.component;

import fr.insee.queen.api.dto.input.CampaignIntegrationInputDto;
import fr.insee.queen.api.controller.integration.component.builder.CampaignBuilder;
import fr.insee.queen.api.controller.integration.component.creator.CampaignCreator;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@AllArgsConstructor
@Slf4j
public class CampaignIntegrationComponent {
    private final CampaignBuilder campaignBuilder;
    private final CampaignCreator campaignCreator;

    public Pair<String, IntegrationResultUnitDto> process(ZipEntry campaignXmlFile, ZipFile zf) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, JSONException {
        try {
            CampaignIntegrationInputDto campaign = campaignBuilder.build(zf, campaignXmlFile);
            return Pair.of(campaign.id(), campaignCreator.create(campaign));
        } catch (IntegrationValidationException e) {
            return Pair.of(null, e.resultError());
        }
    }
}
