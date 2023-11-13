package fr.insee.queen.api.depositproof.service.generation;

import fr.insee.queen.api.configuration.properties.ApplicationProperties;
import fr.insee.queen.api.depositproof.service.exception.DepositProofException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoToPDFService {
    private final ApplicationProperties applicationProperties;

    public File transformFoToPdf(File foFile) throws IOException {
        log.info("foFile = {}", foFile.getPath());
        Path tempDirectoryPath = Path.of(applicationProperties.tempFolder());
        File outFilePDF = Files.createTempFile(tempDirectoryPath, UUID.randomUUID().toString(), ".pdf").toFile();
        try(FileOutputStream fileOuputStream = new FileOutputStream(outFilePDF);
                OutputStream out = new BufferedOutputStream(fileOuputStream)) {
            InputStream isXconf = getClass().getResourceAsStream("/pdf/fop.xconf");
            URI folderBase = getClass().getResource("/pdf/").toURI();
            FopFactory fopFactory = FopFactory.newInstance(folderBase, isXconf);
            fopFactory.getFontManager().setCacheFile(Path.of(System.getProperty("java.io.tmpdir") + "/fop.cache").toUri());
            Fop fop = fopFactory.newFop(MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            Source src = new StreamSource(foFile);
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DepositProofException();
        }
        return outFilePDF;
    }
}
