package fr.insee.queen.infrastructure.depositproof.generation;

import fr.insee.queen.infrastructure.depositproof.exception.DepositProofException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.TransformerFactoryImpl;
import org.springframework.stereotype.Component;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FOGeneration {
    public static final String UNITE = "unite";
    public static final String TITRE = "campaignLabel";
    public static final String DATE = "date";
    private final String tempFolder;

    public File generateFo(String date, String campaignLabel, String userId) throws IOException {
        Path tempDirectoryPath = Path.of(tempFolder);
        File outputFile = Files.createTempFile(tempDirectoryPath, UUID.randomUUID().toString(), ".fo").toFile();
        log.info(outputFile.getAbsolutePath());
        try (InputStream inputStream = getInputStreamFromPath("/xsl/empty.xml");
             OutputStream outputStream = new FileOutputStream(outputFile);
             InputStream xsl = getInputStreamFromPath("/xsl/common.xsl")) {
            xslGenerateFo(inputStream, xsl, outputStream, date, campaignLabel, userId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DepositProofException();
        }
        return outputFile;
    }

    private void xslGenerateFo(InputStream input,
                               InputStream xslSheet,
                               OutputStream outputStream,
                               String date,
                               String campaignLabel,
                               String userId) throws TransformerException {
        TransformerFactory tFactory = new TransformerFactoryImpl();
        tFactory.setURIResolver(new ClasspathURIResolver());
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
        transformer.setURIResolver(new ClasspathURIResolver());
        transformer.setParameter(UNITE, userId);
        transformer.setParameter(TITRE, campaignLabel);
        transformer.setParameter(DATE, date);
        transformer.transform(new StreamSource(input), new StreamResult(outputStream));
    }

    private InputStream getInputStreamFromPath(String path) {
        return getClass().getResourceAsStream(path);
    }
}
