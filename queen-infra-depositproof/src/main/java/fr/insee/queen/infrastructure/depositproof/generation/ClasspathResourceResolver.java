package fr.insee.queen.infrastructure.depositproof.generation;

import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;

import java.io.*;
import java.net.URI;

public class ClasspathResourceResolver implements ResourceResolver {
    @Override
    public Resource getResource(URI uri) {
        String uriString = uri.toASCIIString();
        if(uriString.startsWith("classpath:")) {
            String substring = uriString.substring(10);
            InputStream resourceAsStream = ClasspathResourceResolver.class.getResourceAsStream(substring);
            return (new Resource(resourceAsStream));
        }
        return new Resource(ClasspathResourceResolver.class.getResourceAsStream(uriString));
    }

    @Override
    public OutputStream getOutputStream(URI uri) throws IOException {
        return new FileOutputStream(new File(uri));
    }
}