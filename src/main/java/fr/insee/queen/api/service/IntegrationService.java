package fr.insee.queen.api.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import fr.insee.queen.api.domain.Campaign;
import fr.insee.queen.api.dto.integration.IntegrationResultDto;

public interface IntegrationService extends BaseService<Campaign, String> {
	
	IntegrationResultDto integrateContext(MultipartFile file) throws IOException, SAXException, XPathExpressionException, ParserConfigurationException;
	
    
}
