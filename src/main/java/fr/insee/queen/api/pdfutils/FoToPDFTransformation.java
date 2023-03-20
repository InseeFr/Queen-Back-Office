package fr.insee.queen.api.pdfutils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoToPDFTransformation {
    static Logger logger = LoggerFactory.getLogger(FoToPDFTransformation.class);

    public File transformFoToPdf(File foFile) throws Exception {

	logger.info("foFile = " + foFile.getPath());
	File outFilePDF = File.createTempFile("pdf-file", ".pdf");
	try {

	    File conf = new File(FoToPDFTransformation.class.getResource("/pdf/fop.xconf").toURI());
	    InputStream isXconf = new FileInputStream(conf);


	    URI folderBase = FoToPDFTransformation.class.getResource("/pdf/").toURI();
	    FopFactory fopFactory = FopFactory.newInstance(folderBase, isXconf);

	    OutputStream out = new BufferedOutputStream(new FileOutputStream(outFilePDF));
	    logger.info("outFilePDF" + foFile.getPath());


	    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
	    
	    logger.info("java.io.tmpdir = " +Path.of(System.getProperty("java.io.tmpdir ")));
	    	    
	    fopFactory.getFontManager().setCacheFile(Path.of(System.getProperty("java.io.tmpdir")).toUri());

	    TransformerFactory factory = TransformerFactory.newInstance();

	    Transformer transformer = factory.newTransformer();

	    Source src = new StreamSource(foFile);

	    Result res = new SAXResult(fop.getDefaultHandler());

	    transformer.transform(src, res);

	    out.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error("Error during fo to pdf transformation :" + e.getMessage());
	}
	return outFilePDF;
    }
}
