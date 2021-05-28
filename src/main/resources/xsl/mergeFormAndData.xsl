<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms" version="2.0">

    <xsl:param name="data-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xsl:template match="/">
        <xsl:apply-templates select="/xhtml:html"/>
    </xsl:template>

    <xsl:template match="node()|@*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <!-- When the instance corresponding to the questionnaire data is found, it is replaced by the retrieved instance. -->
    <xsl:template match="form[parent::xf:instance[@id='fr-form-instance']]">
        <xsl:copy-of select="$data-node"/>
    </xsl:template>

</xsl:stylesheet>