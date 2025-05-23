package fr.insee.queen.application.integration.component.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterators;
import fr.insee.queen.application.integration.component.builder.schema.SchemaComponent;
import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.application.integration.dto.input.CampaignIntegrationData;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.service.IntegrationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handle the integration of a campaign
 */
@Component
@Slf4j
@AllArgsConstructor
public class IntegrationCampaignBuilder implements CampaignBuilder {
    private final SchemaComponent schemaComponent;
    private final Validator validator;
    private final IntegrationService integrationService;
    private final ObjectMapper mapper;
    private static final String LABEL = "Label";
    private static final String METADATA = "Metadata";
    private static final String SENSITIVITY = "Sensitivity";
    public static final String CAMPAIGN_XML = "campaign.xml";
    public static final String CAMPAIGN_JSON = "campaign.json";

    @Override
    public IntegrationResultUnitDto build(ZipFile integrationZipFile, boolean isXmlIntegration) {
        if(isXmlIntegration) {
            return buildXmlCampaign(integrationZipFile);
        }
        return buildCampaign(integrationZipFile);
    }

    private IntegrationResultUnitDto buildXmlCampaign(ZipFile zf) {
        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(zf, CAMPAIGN_XML, "campaign_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            return ex.getResultError();
        }

        Document doc;
        try {
            doc = schemaComponent.buildDocument(zf.getInputStream(zf.getEntry(CAMPAIGN_XML)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return IntegrationResultUnitDto.integrationResultUnitError(null, e.getMessage());
        }

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        String id;
        try {
            XPathExpression expr = xpath.compile("/Campaign/Id/text()");
            id = expr.evaluate(doc, XPathConstants.STRING).toString();
        } catch (XPathExpressionException e) {
            log.error("Error when parsing campaign xml", e);
            return IntegrationResultUnitDto.integrationResultUnitError(null, IntegrationResultLabel.CAMPAIGN_ID_INCORRECT);
        }

        NodeList metadataTags = doc.getElementsByTagName(METADATA);
        NodeList labelTags = doc.getElementsByTagName(LABEL);
        NodeList sensitivityTags = doc.getElementsByTagName(SENSITIVITY);

        ObjectNode metadataValue = mapper.createObjectNode();
        if (metadataTags.getLength() > 0) {
            log.info("Setting metadata for campaign {}", id);
            try {
                metadataValue = convertMetadataValueToObjectNode(metadataTags.item(0));
            } catch (JsonProcessingException e) {
                log.error("Error when converting metadata", e);
                return IntegrationResultUnitDto.integrationResultUnitError(null, e.getMessage());
            }
        }

        String label = "";
        if (labelTags.getLength() > 0) {
            log.info("Setting label for campaign {}", id);
            label = labelTags.item(0).getTextContent();
        }

        CampaignSensitivity campaignSensitivity = CampaignSensitivity.NORMAL;
        if (sensitivityTags.getLength() > 0) {
            log.info("Setting sensitivity for campaign {}", id);
            String sensitivity = sensitivityTags.item(0).getTextContent();
            campaignSensitivity = CampaignSensitivity.valueOf(sensitivity);
        }
        CampaignIntegrationData campaign = new CampaignIntegrationData(id, label, campaignSensitivity, metadataValue);
        return buildCampaign(campaign);
    }

    private IntegrationResultUnitDto buildCampaign(ZipFile zf) {
        try {
            schemaComponent.throwExceptionIfJsonDataFileNotValid(zf, CAMPAIGN_JSON, SchemaType.CAMPAIGN_INTEGRATION);
            ZipEntry zipCampaignFile = zf.getEntry(CAMPAIGN_JSON);
            CampaignIntegrationData campaign = mapper.readValue(zf.getInputStream(zipCampaignFile), CampaignIntegrationData.class);
            if(campaign.sensitivity() == null) {
                campaign = new CampaignIntegrationData(campaign.id(), campaign.label(), CampaignSensitivity.NORMAL, campaign.metadata());
            }
            return buildCampaign(campaign);
        } catch (IntegrationValidationException ex) {
            return ex.getResultError();
        }  catch (IOException e) {
            return IntegrationResultUnitDto.integrationResultUnitError(
                    null,
                    String.format(IntegrationResultLabel.JSON_PARSING_ERROR, CAMPAIGN_JSON));
        }
    }

    private IntegrationResultUnitDto buildCampaign(CampaignIntegrationData campaign) {
        Set<ConstraintViolation<CampaignIntegrationData>> violations = validator.validate(campaign);
        if (!violations.isEmpty()) {
            StringBuilder violationMessage = new StringBuilder();
            for (ConstraintViolation<CampaignIntegrationData> violation : violations) {
                violationMessage
                        .append(violation.getPropertyPath().toString())
                        .append(": ")
                        .append(violation.getMessage())
                        .append(". ");
            }
            return IntegrationResultUnitDto.integrationResultUnitError(campaign.id(), violationMessage.toString());
        }
        IntegrationResult result = integrationService.create(CampaignIntegrationData.toModel(campaign));
        return IntegrationResultUnitDto.fromModel(result);
    }

    private ObjectNode convertMetadataValueToObjectNode(Node xmlNode) throws JsonProcessingException {
        String jsonString = XML.toJSONObject(toString(xmlNode)).toString();
        ObjectNode metadataObject = mapper.readValue(jsonString, ObjectNode.class);
        if (metadataObject.get(METADATA).isEmpty()) {
            return JsonNodeFactory.instance.objectNode();
        }
        return (ObjectNode) removeArrayLevel(metadataObject.get(METADATA), mapper);
    }

    private JsonNode removeArrayLevel(JsonNode node, ObjectMapper mapper) {
        if (node == null || node.isValueNode()) {
            return node;
        }
        if (node.isArray()) {
            ArrayNode arrNode = mapper.createArrayNode();
            for (int i = 0; i < node.size(); i++) {
                arrNode.add(removeArrayLevel(node.get(i), mapper));
            }
            return arrNode;
        }
        if (node.isObject()) {
            if (Iterators.size(node.elements()) == 1 && node.elements().next().isArray()) {
                String keyName = node.fieldNames().next();
                return removeArrayLevel(node.get(keyName), mapper);
            } else {
                ObjectNode objNode = mapper.createObjectNode();
                Iterator<Map.Entry<String, JsonNode>> it = node.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> e = it.next();
                    objNode.set(e.getKey(), removeArrayLevel(e.getValue(), mapper));
                }
                return objNode;
            }
        }
        return node;
    }

    private String toString(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null.");
        }
        try {
            // Remove unwanted whitespaces
            node.normalize();
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile("//text()[normalize-space()='']");
            NodeList nodeList = (NodeList) expr.evaluate(node, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node nd = nodeList.item(i);
                nd.getParentNode().removeChild(nd);
            }

            // Create and setup transformer
            System.setProperty("javax.xml.transform.TransformerFactory",
                    "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Turn the node into a string
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException | XPathExpressionException e) {
            return null;
        }
    }
}

