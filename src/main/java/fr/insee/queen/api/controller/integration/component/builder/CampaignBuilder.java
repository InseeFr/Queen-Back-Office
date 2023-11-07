package fr.insee.queen.api.controller.integration.component.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterators;
import fr.insee.queen.api.controller.integration.component.IntegrationResultLabel;
import fr.insee.queen.api.controller.integration.component.SchemaComponent;
import fr.insee.queen.api.controller.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.api.dto.input.CampaignIntegrationInputDto;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.service.integration.IntegrationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.XML;
import org.springframework.data.util.Pair;
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
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@Slf4j
@AllArgsConstructor
public class CampaignBuilder {
    private final SchemaComponent schemaComponent;
    private final Validator validator;
    private final IntegrationService integrationService;
    private final ObjectMapper objectMapper;
    private static final String LABEL = "Label";
    private static final String METADATA = "Metadata";
    public static final String CAMPAIGN_XML = "campaign.xml";

    public Pair<Optional<String>, IntegrationResultUnitDto> build(ZipFile zf, ZipEntry campaignXmlFile) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, JSONException {
        if(campaignXmlFile == null) {
            return Pair.of(Optional.empty(), new IntegrationResultErrorUnitDto(
                    CAMPAIGN_XML,
                    String.format(IntegrationResultLabel.FILE_NOT_FOUND, CAMPAIGN_XML)));
        }

        try {
            schemaComponent.throwExceptionIfXmlDataFileNotValid(zf, campaignXmlFile, "campaign_integration_template.xsd");
        } catch (IntegrationValidationException ex) {
            return Pair.of(Optional.empty(), ex.resultError());
        }

        return buildCampaign(zf, campaignXmlFile);
    }


    private Pair<Optional<String>, IntegrationResultUnitDto> buildCampaign(ZipFile zf, ZipEntry campaignXmlFile) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, JSONException {

        Document doc = schemaComponent.buildDocument(zf.getInputStream(campaignXmlFile));

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/Campaign/Id/text()");

        String id = expr.evaluate(doc, XPathConstants.STRING).toString().toUpperCase();

        NodeList metadataTags = doc.getElementsByTagName(METADATA);
        NodeList labelTags = doc.getElementsByTagName(LABEL);

        ObjectNode metadataValue = objectMapper.createObjectNode();
        if(metadataTags.getLength() > 0) {
            log.info("Setting metadata for campaign {}", id);
            metadataValue = convertMetadataValueToObjectNode(metadataTags.item(0));
        }

        String label = "";
        if(labelTags.getLength() > 0) {
            log.info("Setting label for campaign {}", id);
            label = labelTags.item(0).getTextContent();
        }

        return buildCampaign(id, label, metadataValue);
    }

    private Pair<Optional<String>, IntegrationResultUnitDto> buildCampaign(String id, String label, ObjectNode metadata) {
        CampaignIntegrationInputDto campaign = new CampaignIntegrationInputDto(id, label, metadata);
        Set<ConstraintViolation<CampaignIntegrationInputDto>> violations = validator.validate(campaign);
        if (!violations.isEmpty()) {
            StringBuilder violationMessage = new StringBuilder();
            for (ConstraintViolation<CampaignIntegrationInputDto> violation : violations) {
                violationMessage
                        .append(violation.getPropertyPath().toString())
                        .append(": ")
                        .append(violation.getMessage())
                        .append(". ");
            }
            return Pair.of(Optional.empty(), new IntegrationResultErrorUnitDto(campaign.id(), violationMessage.toString()));
        }
        return integrationService.create(campaign);
    }

    private ObjectNode convertMetadataValueToObjectNode(Node xmlNode) throws JsonProcessingException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = XML.toJSONObject(toString(xmlNode)).toString();
        ObjectNode metadataObject = mapper.readValue(jsonString, ObjectNode.class);
        return (ObjectNode) removeArrayLevel(metadataObject.get(METADATA), mapper);
    }

    private JsonNode removeArrayLevel(JsonNode node, ObjectMapper mapper) {
        if(node == null || node.isValueNode()) {
            return node;
        }
        if(node.isArray()) {
            ArrayNode arrNode = mapper.createArrayNode();
            for (int i = 0; i < node.size(); i++) {
                arrNode.add(removeArrayLevel(node.get(i),mapper));
            }
            return arrNode;
        }
        if(node.isObject()) {
            if(Iterators.size(node.elements()) == 1 && node.elements().next().isArray()) {
                String keyName = node.fieldNames().next();
                return removeArrayLevel(node.get(keyName), mapper);
            }
            else {
                ObjectNode objNode = mapper.createObjectNode();
                Iterator<Map.Entry<String, JsonNode>> it = node.fields();
                while(it.hasNext()) {
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
            NodeList nodeList = (NodeList)expr.evaluate(node, XPathConstants.NODESET);

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
            Transformer transformer =  tf.newTransformer();
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

