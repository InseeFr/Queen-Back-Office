package fr.insee.queen.api.pdfutils;


import java.io.InputStream;

public final class Constants {
    // ----- Folders
    public static final String TRANSFORMATION_XSL_COMMON = "/xsl/common.xsl";
    public static final String TRANSFORMATION_XSL_MERGE_DATA_FORM = "/xsl/mergeFormAndData.xsl";
    public static final String EMPTY_XML = "/xsl/empty.xml";

    public static final String FOP_CONF = "/pdf/fop.xconf";

    public static InputStream getEmptyXml(){
        try {
            return Constants.class.getResourceAsStream(EMPTY_XML);
        } catch (Exception e) {
            return null;
        }
    }

    public static InputStream getInputStreamFromPath(String path) {
        try {
            return Constants.class.getResourceAsStream(path);
        } catch (Exception e) {
            return null;
        }
    }
}