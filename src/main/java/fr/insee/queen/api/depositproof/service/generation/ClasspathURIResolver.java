package fr.insee.queen.api.depositproof.service.generation;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Use for controlling the resolution of includes
 * FIXME we need to urgently change the includes to match a simpler scheme
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
