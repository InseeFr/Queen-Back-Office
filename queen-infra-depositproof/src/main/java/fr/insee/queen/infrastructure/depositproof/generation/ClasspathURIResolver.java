package fr.insee.queen.infrastructure.depositproof.generation;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Use for controlling the resolution of includes
 * i.e. import statements href are equal to <code>/path/to/resources/directory</code>
 */
public class ClasspathURIResolver implements URIResolver {
    @Override
    public Source resolve(String href, String base) {
        String resolvedHref = "";
        if (href.endsWith(".xsl")) {
            resolvedHref = "/xsl/" + href;
        }
        return new StreamSource(getClass().getResourceAsStream(resolvedHref));
    }

}
