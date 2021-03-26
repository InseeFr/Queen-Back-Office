package fr.insee.queen.api.pdfutils;

import org.apache.fop.apps.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;

public class FoToPDFTransformation {
    static Logger logger = LoggerFactory.getLogger(FoToPDFTransformation.class);

    public File transformFoToPdf(File foFile) throws Exception {
        File outFilePDF = File.createTempFile("pdf-file",".pdf");
        try{
            InputStream isXconf = Constants.getInputStreamFromPath(Constants.FOP_CONF);
            URI folderBase = FoToPDFTransformation.class.getResource("/pdf/").toURI();
            logger.info("Uri folderBase :"+folderBase);
            FopFactory fopFactory = FopFactory.newInstance(folderBase,isXconf);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(outFilePDF));
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF,foUserAgent, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            Source src = new StreamSource(foFile);
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
            out.close();
        } catch (Exception e){
            logger.error("Error during fo to pdf transformation :"+e.getMessage());
        }
        return outFilePDF;
    }
}
