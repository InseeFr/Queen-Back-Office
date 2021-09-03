package fr.insee.queen.api.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterators;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.domain.IntegrationStatus;
import fr.insee.queen.api.domain.Metadata;
import fr.insee.queen.api.domain.Nomenclature;
import fr.insee.queen.api.domain.QuestionnaireModel;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.NomenclatureRepository;
import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.service.IntegrationService;

@Service
@Transactional
public class IntegrationServiceImpl implements IntegrationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationServiceImpl.class);

	@Autowired
    protected CampaignRepository campaignRepository;
    
	@Autowired
    protected QuestionnaireModelRepository questionnaireModelRepository;
    
    @Autowired
    private NomenclatureRepository nomenclatureRepository;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String CAMPAIGN_XML = "campaign.xml";
    private static final String NOMENCLATURES_XML = "nomenclatures.xml";
    private static final String QUESTIONNAIREMODELS_XML = "questionnaireModels.xml";
    private static final String LABEL = "Label";
	private static final String METADATA = "Metadata";
	private static final String ID = "Id";
	private static final String FILENAME = "FileName";
	private static final String CAMPAIGN_ID = "CampaignId";
	private static final String NOMENCLATURE = "Nomenclature";
	
	public IntegrationResultDto integrateContext(MultipartFile file) throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
	    String fileName = file.getOriginalFilename();
	    if (fileName!=null) {
	    	fileName = fileName.replace(".zip", "");
	    }
	    File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
	    try(FileOutputStream o = new FileOutputStream(zip)){
	    	IOUtils.copy(file.getInputStream(), o);
		    return doIntegration(zip);
	    }
	    catch(IOException e) {
	    	throw new IOException(Arrays.toString(e.getStackTrace()));
	    }
	}
	
	private IntegrationResultDto doIntegration(File zip) throws ParserConfigurationException, SAXException, XPathExpressionException {
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
		        if(entry.getName().equals(CAMPAIGN_XML)) {
		        	campaignXmlFile = entry;
		        }
		        else if(entry.getName().equals(NOMENCLATURES_XML)) {
		        	nomenclaturesXmlFile = entry;
		        }
		        else if(entry.getName().equals(QUESTIONNAIREMODELS_XML)) {
		        	questionnaireModelsXmlFile = entry;
		        }
		        else if(Pattern.matches(nomenclaturesPattern,entry.getName())){
		        	nomenclatureJsonFiles.put(entry.getName(), entry);
		        }
		        else if(Pattern.matches(questionnairesPattern,entry.getName())){
		        	questionnaireModelJsonFiles.put(entry.getName(), entry);
		        }
		    }	    
			
			// Nomenclatures process
		    validateAndProcessNomenclatures(zf, nomenclaturesXmlFile, nomenclatureJsonFiles, result);
			
			// Campaign process
		    validateAndProcessCampaign(zf, campaignXmlFile, result);
			
			// Questionnaire models process
			validateAndProcessQuestionnaireModels(zf, questionnaireModelsXmlFile, questionnaireModelJsonFiles, result);
			
			
		    return result;
	    }
	    catch(IOException e) {
	    	return null;
	    }
	}
	
	private void validateAndProcessNomenclatures(ZipFile zf, ZipEntry nomenclaturesXmlFile, 
			HashMap<String, ZipEntry> nomenclatureJsonFiles, IntegrationResultDto result) throws ParserConfigurationException, SAXException, IOException {
		if(nomenclaturesXmlFile == null) {
			result.setNomenclatures(new ArrayList<>());
			result.getNomenclatures().add(new IntegrationResultUnitDto(
					NOMENCLATURES_XML, 
					IntegrationStatus.ERROR, 
					"No file nomenclatures.xml found"));
		} else {
			boolean validation = false;
			try {
				validation = validateAgainstSchema(zf.getInputStream(nomenclaturesXmlFile), "nomenclatures_integration_template.xsd");
			}
			catch(Exception ex) {
				result.setNomenclatures(new ArrayList<>());
				result.getNomenclatures().add(new IntegrationResultUnitDto(
						NOMENCLATURES_XML, 
						IntegrationStatus.ERROR, 
						"File nomenclatures.xml does not fit the required template (" + ex.getMessage() + ")"));
			}
			if(validation) {
				processNomenclatures(zf, nomenclaturesXmlFile, nomenclatureJsonFiles, result);
			}
		}
	}
	
	private void validateAndProcessCampaign(ZipFile zf, ZipEntry campaignXmlFile,
			IntegrationResultDto result) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		if(campaignXmlFile == null) {
			result.setCampaign(new IntegrationResultUnitDto(CAMPAIGN_XML, 
					IntegrationStatus.ERROR, 
					"No file campaign.xml found"));
		} else {
			boolean validation = false;
			try {
				validation = validateAgainstSchema(zf.getInputStream(campaignXmlFile), "campaign_integration_template.xsd");
			}
			catch(Exception ex) {
				result.setCampaign(new IntegrationResultUnitDto(CAMPAIGN_XML, 
						IntegrationStatus.ERROR, 
						"File campaign.xml does not fit the required template (" + ex.getMessage() + ")"));
			}
			if(validation) {
				processCampaign(zf, campaignXmlFile, result);
			}
		}
	}
	
	private void validateAndProcessQuestionnaireModels(ZipFile zf, ZipEntry questionnaireModelsXmlFile, 
			HashMap<String, ZipEntry> questionnaireModelJsonFiles, IntegrationResultDto result) throws ParserConfigurationException, SAXException, IOException {
		if(questionnaireModelsXmlFile == null) {
			result.setQuestionnaireModels(new ArrayList<>());
			result.getQuestionnaireModels().add(new IntegrationResultUnitDto(
					QUESTIONNAIREMODELS_XML, 
					IntegrationStatus.ERROR, 
					"No file questionnaireModels.xml found"));
		} else {
			boolean validation = false;
			try {
				validation = validateAgainstSchema(zf.getInputStream(questionnaireModelsXmlFile), "questionnaireModels_integration_template.xsd");
			}
			catch(Exception ex) {
				result.setQuestionnaireModels(new ArrayList<>());
				result.getQuestionnaireModels().add(new IntegrationResultUnitDto(
						QUESTIONNAIREMODELS_XML, 
						IntegrationStatus.ERROR, 
						"File questionnaireModels.xml does not fit the required template (" + ex.getMessage() + ")"));
			}
			if(validation) {
				processQuestionnaireModels(zf, questionnaireModelsXmlFile, questionnaireModelJsonFiles, result);
			}
		}
	}
	
	
	private boolean validateAgainstSchema(InputStream xmlStream, String schemaFileName) throws SAXException, IOException {
		File template = new File(getClass().getClassLoader().getResource("templates//" + schemaFileName).getFile());
		SchemaFactory facto = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		facto.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		facto.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		Schema schema = facto.newSchema(template);
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(xmlStream));
		return true;
	}
	
	
	
	private void processCampaign(ZipFile zf, ZipEntry campaignXmlFile, IntegrationResultDto result) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(zf.getInputStream(campaignXmlFile));
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("/Campaign/Id/text()");
	    
		String id = expr.evaluate(doc, XPathConstants.STRING).toString().toUpperCase();
		Optional<Campaign> campaignOpt = campaignRepository.findById(id);
		Campaign campaign;
		if(campaignOpt.isPresent()) {
			campaign = campaignOpt.get();
			result.setCampaign(new IntegrationResultUnitDto(id, IntegrationStatus.UPDATED, null));
		}
		else {
			LOGGER.info("Creating campaign {}", id);
			campaign = new Campaign();
			campaign.setId(id);
			result.setCampaign(new IntegrationResultUnitDto(id, IntegrationStatus.CREATED, null));
		}
		
		NodeList metadataTags = doc.getElementsByTagName(METADATA);
		NodeList labelTags = doc.getElementsByTagName(LABEL);
		
		if(metadataTags.getLength() > 0) {
			LOGGER.info("Setting metadata for campaign {}", id);
			JsonNode metadata = customConvertMetadataToJson(metadataTags.item(0));
			if(campaign.getMetadata()==null) {
				Metadata meta = new Metadata();
				meta.setCampaign(campaign);
				campaign.setMetadata(meta);
			}
			campaign.getMetadata().setValue(metadata);
		}
		
		if(labelTags.getLength() > 0) {
			LOGGER.info("Setting label for campaign {}", id);
			String label = labelTags.item(0).getTextContent();
			campaign.setLabel(label);
		}
		
		campaignRepository.save(campaign);
	}
	
	public JsonNode customConvertMetadataToJson(Node xmlNode) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonString = XML.toJSONObject(toString(xmlNode, true, true)).toString();
		JsonNode jsonObj = mapper.readTree(jsonString);
		
		return removeArrayLevel(jsonObj.get(METADATA), mapper);
	}
	
	public JsonNode removeArrayLevel(JsonNode node, ObjectMapper mapper) {
		
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
				Iterator<Entry<String, JsonNode>> it = node.fields();
				while(it.hasNext()) {
					Entry<String, JsonNode> e = it.next();
					objNode.set(e.getKey(), removeArrayLevel(e.getValue(), mapper));
				}
				return objNode;
			}
		}
		return node;
	}
	
	private void processNomenclatures(ZipFile zf, ZipEntry nomenclaturesXmlFile,
			HashMap<String, ZipEntry> nomenclatureJsonFiles, IntegrationResultDto result) throws ParserConfigurationException, SAXException, IOException {
		ArrayList<IntegrationResultUnitDto> results = new ArrayList<>();
		result.setNomenclatures(results);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(zf.getInputStream(nomenclaturesXmlFile));
		NodeList nomenclatureNodes = doc.getElementsByTagName("Nomenclatures").item(0).getChildNodes();
		 for (int i = 0; i < nomenclatureNodes.getLength(); i++) {
			 if(nomenclatureNodes.item(i).getNodeType() == Node.ELEMENT_NODE){
				 Element nomenclature = (Element) nomenclatureNodes.item(i);
				 processNomenclature(zf, nomenclature, nomenclatureJsonFiles, results);
			 }
			 
		 }
	}
	
	private void processNomenclature(ZipFile zf, Element nomenclature, 
		 HashMap<String, ZipEntry> nomenclatureJsonFiles, 
		 ArrayList<IntegrationResultUnitDto> results) {
		 String nomenclatureId = nomenclature.getElementsByTagName(ID).item(0).getTextContent();
		 String nomenclatureLabel = nomenclature.getElementsByTagName(LABEL).item(0).getTextContent();
		 String nomenclatureFilename = nomenclature.getElementsByTagName(FILENAME).item(0).getTextContent();
		 
		 Optional<Nomenclature> nomOpt = nomenclatureRepository.findById(nomenclatureId);
		 if(nomOpt.isPresent()) {
			 LOGGER.info("Nomenclature {} already exists", nomenclatureId);
			 results.add(new IntegrationResultUnitDto(
					 nomenclatureId, 
					 IntegrationStatus.ERROR, 
					 "A nomenclature with this id already exists")
				);
		 }
		 else {
			 ZipEntry nomenclatureValueEntry = nomenclatureJsonFiles.get("nomenclatures/" +nomenclatureFilename);
			 if(nomenclatureValueEntry != null) {
				JsonNode nomenclatureValue;
				try {
					 nomenclatureValue = objectMapper.readTree(zf.getInputStream(nomenclatureValueEntry));
					 Nomenclature nomen = new Nomenclature();
					 nomen.setId(nomenclatureId);
					 nomen.setLabel(nomenclatureLabel);
					 nomen.setValue(nomenclatureValue);
					 
					 LOGGER.info("Creating nomenclature {}", nomenclatureId);
					 results.add(new IntegrationResultUnitDto(
							 nomenclatureId, 
							 IntegrationStatus.CREATED, 
							 null));
					 nomenclatureRepository.save(nomen);
				} catch (IOException e) {
					LOGGER.info("Could not parse json in file {}", nomenclatureFilename);
					results.add(new IntegrationResultUnitDto(
							 nomenclatureId, 
							 IntegrationStatus.ERROR, 
							 "Could not parse json in file '" + nomenclatureFilename + "'")
						);
				}
			 }
			 else {
				 LOGGER.info("Nomenclature file {} could not be found in input zip", nomenclatureFilename );
				 results.add(new IntegrationResultUnitDto(
						 nomenclatureId, 
						 IntegrationStatus.ERROR, 
						 "Nomenclature file '" + nomenclatureFilename + "' could not be found in input zip")
					);
			 }
		 }
	}
	
	private void processQuestionnaireModels(ZipFile zf, ZipEntry questionnaireModelsXmlFile,
			HashMap<String, ZipEntry> questionnaireModelJsonFiles, IntegrationResultDto result) throws ParserConfigurationException, SAXException, IOException {
		ArrayList<IntegrationResultUnitDto> results = new ArrayList<>();
		result.setQuestionnaireModels(results);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(zf.getInputStream(questionnaireModelsXmlFile));
		NodeList qmNodes = doc.getElementsByTagName("QuestionnaireModels").item(0).getChildNodes();
		 for (int i = 0; i < qmNodes.getLength(); i++) {
			 if(qmNodes.item(i).getNodeType() == Node.ELEMENT_NODE){
				 Element qm = (Element) qmNodes.item(i);
				 processQuestionnaireModel(zf, qm, results, questionnaireModelJsonFiles);
			 }
			 
		 }
	}
	
	private void processQuestionnaireModel(ZipFile zf, Element qm, 
		 ArrayList<IntegrationResultUnitDto> results, 
		 HashMap<String, ZipEntry> questionnaireModelJsonFiles) {
		 String qmId = qm.getElementsByTagName(ID).item(0).getTextContent();
		 String qmLabel = qm.getElementsByTagName(LABEL).item(0).getTextContent();
		 String qmFilename = qm.getElementsByTagName(FILENAME).item(0).getTextContent();
		 String campaignId = qm.getElementsByTagName(CAMPAIGN_ID).item(0).getTextContent();
		 NodeList requiredNomNodes = qm.getElementsByTagName(NOMENCLATURE);
		 ArrayList<String> requiredNomenclatureIds = (ArrayList<String>) IntStream.range(0, requiredNomNodes.getLength())
				 .filter(j-> requiredNomNodes.item(j).getNodeType() == Node.ELEMENT_NODE)
				 .mapToObj(j -> requiredNomNodes.item(j).getTextContent())
				 .collect(Collectors.toList());
		 // Checking if campaign exists
		 Campaign campaign;
		 Optional<Campaign> campaignOpt = campaignRepository.findById(campaignId);
		 if(!campaignOpt.isPresent()) {
			 LOGGER.info("Could not create Questionnaire model {}, campaign {} does not exist", qmId, campaignId);
			 results.add(new IntegrationResultUnitDto(
					 qmId, 
					 IntegrationStatus.ERROR, 
					 "The campaign '" + campaignId + "' does not exist")
				);
			 return; 
		 }
		 campaign = campaignOpt.get();
		 
		 // Checking if required nomenclatures exist
		 ArrayList<Nomenclature> requiredNomenclatures = new ArrayList<>();
		 for(String id : requiredNomenclatureIds) {
			 Optional<Nomenclature> nomenclatureOpt = nomenclatureRepository.findById(id);
			 if(nomenclatureOpt.isPresent()) {
				 requiredNomenclatures.add(nomenclatureOpt.get());
			 }
			 else {
				 LOGGER.info("Could not create Questionnaire model {}, nomenclature {} does not exist", qmId, id);
				 results.add(new IntegrationResultUnitDto(
						 qmId, 
						 IntegrationStatus.ERROR, 
						 "The nomenclature '" + id + "' does not exist")
					);
				 return;
			 }
		 }
		 
		 Optional<QuestionnaireModel> qmOpt = questionnaireModelRepository.findById(qmId);
		 QuestionnaireModel questionnaireModel;
		 IntegrationStatus status;
		 if(qmOpt.isPresent()) {
			 LOGGER.info("QuestionnaireModel {} already exists", qmId);
			 questionnaireModel = qmOpt.get();
			 status = IntegrationStatus.UPDATED;
		 }
		 else {
			 questionnaireModel = new QuestionnaireModel();
			 questionnaireModel.setId(qmId);
			 status = IntegrationStatus.CREATED;
		 }
		 ZipEntry qmValueEntry = questionnaireModelJsonFiles.get("questionnaireModels/" +qmFilename);
		 if(qmValueEntry != null) {
			JsonNode qmValue;
			try {
				 qmValue = objectMapper.readTree(zf.getInputStream(qmValueEntry));
				 questionnaireModel.setLabel(qmLabel);
				 questionnaireModel.setValue(qmValue);
				 questionnaireModel.getNomenclatures().clear();
				 questionnaireModel.getNomenclatures().addAll(requiredNomenclatures);
				 
				 LOGGER.info("Creating questionnaire model {}", qmId);
				 results.add(new IntegrationResultUnitDto(
						 qmId, 
						 status, 
						 null)
					);

				 questionnaireModel.setCampaign(campaign);

				 questionnaireModelRepository.save(questionnaireModel);
				 
				 // Necessary for mongoDB
				 Set<QuestionnaireModel> qms = campaign.getQuestionnaireModels();
				 if(qms.stream().filter(q -> q.getId().equals(qmId)).collect(Collectors.toList()).isEmpty()) {
					 qms.add(questionnaireModel);
					 campaignRepository.save(campaign);
				 }
				 
			} catch (IOException e) {
				LOGGER.info("Could not parse json in file {}", qmFilename);
				results.add(new IntegrationResultUnitDto(
						 qmId, 
						 IntegrationStatus.ERROR, 
						 "Could not parse json in file '" + qmFilename + "'")
					);
			}
		 }
		 else {
			 LOGGER.info("Questionnaire model file {} could not be found in input zip", qmFilename);
			 results.add(new IntegrationResultUnitDto(
					 qmId, 
					 IntegrationStatus.ERROR, 
					 "Questionnaire model file '" + qmFilename + "' could not be found in input zip")
				);
		 }
	}
	
	public static String toString(Node node, boolean omitXmlDeclaration, boolean prettyPrint) {
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
	        TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
	        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
	        Transformer transformer =  tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        
	        if (omitXmlDeclaration) {
	           transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        }

	        if (prettyPrint) {
	           transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	           transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	        }

	        // Turn the node into a string
	        StringWriter writer = new StringWriter();
	        transformer.transform(new DOMSource(node), new StreamResult(writer));
	        return writer.toString();
	    } catch (TransformerException | XPathExpressionException e) {

	        return null;
	    }
	}

}
