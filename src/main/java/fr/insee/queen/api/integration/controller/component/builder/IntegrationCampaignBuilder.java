package fr.insee.queen.api.integration.controller.component.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterators;
import fr.insee.queen.api.integration.controller.component.builder.schema.SchemaComponent;
import fr.insee.queen.api.integration.controller.component.exception.IntegrationValidationException;
import fr.insee.queen.api.integration.controller.dto.input.CampaignIntegrationData;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.api.integration.service.IntegrationService;
import fr.insee.queen.api.integration.service.model.IntegrationResult;
import fr.insee.queen.api.integration.service.model.IntegrationResultLabel;
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
    private final ObjectMapper objectMapper;
    private static final String LABEL = "Label";
    private static final String METADATA = "Metadata";
    public static final String CAMPAIGN_XML = "campaign.xml";

    @Override
    public IntegrationResultUnitDto build(ZipFile integrationZipFile) {
        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(integrationZipFile, CAMPAIGN_XML, "campaign_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            return ex.resultError();
        }
        return buildCampaign(integrationZipFile);
    }

    private IntegrationResultUnitDto buildCampaign(ZipFile zf) {
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
            id = expr.evaluate(doc, XPathConstants.STRING).toString().toUpperCase();
        } catch (XPathExpressionException e) {
            log.error("Error when parsing campaign xml", e);
            return IntegrationResultUnitDto.integrationResultUnitError(null, IntegrationResultLabel.CAMPAIGN_ID_INCORRECT);
        }

        NodeList metadataTags = doc.getElementsByTagName(METADATA);
        NodeList labelTags = doc.getElementsByTagName(LABEL);

        ObjectNode metadataValue = objectMapper.createObjectNode();
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

        return buildCampaign(id, label, metadataValue);
    }

    private IntegrationResultUnitDto buildCampaign(String id, String label, ObjectNode metadata) {
        CampaignIntegrationData campaign = new CampaignIntegrationData(id, label, metadata);
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
        ObjectMapper mapper = new ObjectMapper();
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

