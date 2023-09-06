package fr.insee.queen.api.pdfutils;

import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;


@Slf4j
public class FoToPDFTransformation {
    public File transformFoToPdf(File foFile) throws Exception {

	log.info("foFile = " + foFile.getPath());
	File outFilePDF = File.createTempFile("pdf-file", ".pdf");
	try {

        InputStream isXconf = FoToPDFTransformation.class.getResourceAsStream("/pdf/fop.xconf");

	    URI folderBase = FoToPDFTransformation.class.getResource("/pdf/").toURI();
	    
	    FopFactory fopFactory = FopFactory.newInstance(folderBase, isXconf);
	    
	    fopFactory.getFontManager().setCacheFile(Path.of(System.getProperty("java.io.tmpdir")+"/fop.cache").toUri());

	    OutputStream out = new BufferedOutputStream(new FileOutputStream(outFilePDF));

	    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
	        	    
	    TransformerFactory factory = TransformerFactory.newInstance();

	    Transformer transformer = factory.newTransformer();

	    Source src = new StreamSource(foFile);

	    Result res = new SAXResult(fop.getDefaultHandler());

	    transformer.transform(src, res);

	    out.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    log.error("Error during fo to pdf transformation :" + e.getMessage());
	}
	return outFilePDF;
    }
}
