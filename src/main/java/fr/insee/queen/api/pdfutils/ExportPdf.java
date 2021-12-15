package fr.insee.queen.api.pdfutils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportPdf extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6664666680913562382L;

	static Logger logger = LoggerFactory.getLogger(ExportPdf.class);

    private PDFDepositProofService depositProofService;

    private static String configPath = "/config/properties-local-prod.xml";
    private static String urlXpath = "/properties/property[@name='server-exist-orbeon']/@value";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        String pathDossier = servletContext.getInitParameter("oxf.resources.priority.0.oxf.resources.filesystem.sandbox-directory");
        String urlApi = "";

        try {
            urlApi = XMLUtil.execXpathOnFile(pathDossier.concat(configPath), urlXpath);
        } catch (Exception e) {
            logger.error("Souci lors de la récupération de l'url.");
            logger.error("Path du fichier utilisé : ".concat(configPath));
            logger.error("L'erreur est la suivante :", e);
        }
        logger.info("L'url de l'api exist est la suivante : ".concat(urlApi));
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
            pdfFile.delete();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
