package fr.insee.queen.api.pdfutils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Use for controlling the resolution of includes
 * FIXME we need to urgently change the includes to match a simpler scheme
 * i.e. import statements href are equal to <code>/path/to/resources/directory</code>
 * */
public class ClasspathURIResolver implements URIResolver {

	private static Logger LOGGER = LoggerFactory.getLogger(ClasspathURIResolver.class);

	@Override
	public Source resolve(String href, String base) throws TransformerException {

		String resolvedHref = "";
		if(href.endsWith(".xsl")) resolvedHref = "/xsl/"+href;
		return new StreamSource(ClasspathURIResolver.class.getResourceAsStream(resolvedHref));
	}

}
