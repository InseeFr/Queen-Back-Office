package fr.insee.queen.api.pdfutils;

import net.sf.saxon.TransformerFactoryImpl;

import javax.xml.transform.*;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;

public class XslTransformation {

    public void xslMergeFormAndData(InputStream form, InputStream data, InputStream xslSheet, OutputStream outputStream) throws  TransformerException{
        TransformerFactory tFactory = new TransformerFactoryImpl();
        tFactory.setURIResolver(new ClasspathURIResolver());
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
        transformer.setURIResolver(new ClasspathURIResolver());
        Source source = new StreamSource(data);
        transformer.setParameter(XslParameters.DATA_NODE, source);
        transformer.transform(new StreamSource(form), new StreamResult(outputStream));
    }

    public void xslGenerateFo(InputStream input, 
    		InputStream xslSheet, 
    		OutputStream outputStream, 
    		String date,
			String campaignLabel,
			String idec) throws  TransformerException {
    	TransformerFactory tFactory = new TransformerFactoryImpl();
        tFactory.setURIResolver(new ClasspathURIResolver());
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
        transformer.setURIResolver(new ClasspathURIResolver());
        transformer.setParameter(XslParameters.CAMPAIGN_LABEL, idec);
        transformer.setParameter(XslParameters.TITRE, campaignLabel);
        transformer.setParameter(XslParameters.DATE, date);
        transformer.transform(new StreamSource(input), new StreamResult(outputStream));
    }
}
