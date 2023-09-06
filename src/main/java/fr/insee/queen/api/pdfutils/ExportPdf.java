package fr.insee.queen.api.pdfutils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.nio.file.Files;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ExportPdf extends HttpServlet {

    /**
	 * 
	 */
	@Serial
    private static final long serialVersionUID = -6664666680913562382L;
    
    private PDFDepositProofService depositProofService;

    private static final String CONFIG_PATH = "/config/properties-local-prod.xml";
    private static final String URL_XPATH = "/properties/property[@name='server-exist-orbeon']/@value";


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        String pathDossier = servletContext.getInitParameter("oxf.resources.priority.0.oxf.resources.filesystem.sandbox-directory");
        String urlApi = "";

        try {
            urlApi = XMLUtil.execXpathOnFile(pathDossier.concat(CONFIG_PATH), URL_XPATH);
        } catch (Exception e) {
            log.error("Souci lors de la récupération de l'url.");
            log.error("Path du fichier utilisé : ".concat(CONFIG_PATH));
            log.error("L'erreur est la suivante :", e);
        }
        log.info("L'url de l'api exist est la suivante : ".concat(urlApi));
        depositProofService = new PDFDepositProofService();
    }
    
    public void doExport(HttpServletResponse response,
    		String date, String campaignId, String campaignLabel, String idec)
            throws ServletException, IOException {

        String filename = String.format("%s_%s.pdf", campaignId, idec);
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=\""+filename+"\"");

        try(OutputStream out = response.getOutputStream()){
            depositProofService = new PDFDepositProofService();
            File pdfFile = depositProofService.generatePdf(date,campaignLabel, idec);
            out.write(Files.readAllBytes(pdfFile.toPath()));
            Files.delete(pdfFile.toPath());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
