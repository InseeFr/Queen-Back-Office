<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:saxon="http://saxon.sf.net/" xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xs" version="2.0">
    
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
    
    <!-- La xsl comportant l'ensemble des paramètres surchargeables par les xsl de chaque enquête -->
    <xsl:include href="parametres.xsl"/>
   
    
    <!-- Le template du pdf -->
    <xsl:template match="/">
        
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="page-first-household" page-height="297mm"
                    page-width="210mm" reference-orientation="0" font-family="Liberation Sans"
                    font-size="10pt" font-weight="normal" margin-bottom="5mm" margin-top="5mm"
                    margin-right="5mm" margin-left="5mm">
                    
                    
                    <fo:region-body region-name="region-body" margin-top="10mm"
                        margin-bottom="10mm" margin-right="10mm" margin-left="10mm" column-count="1"/>
                    
                    
                </fo:simple-page-master>
                
            </fo:layout-master-set>
            
            
            <fo:page-sequence font-family="Liberation Sans" font-size="10pt"
                master-reference="page-first-household">
                
                <fo:flow flow-name="region-body">
                    <!--    Blocs Logos et Libellé -->
                    
                    <fo:block-container absolute-position="absolute" left="0mm" top="10mm" width="15mm"
                        height="15mm">
                        <fo:block>
                            <fo:external-graphic content-width="scale-to-fit" width="15mm"
                                scaling="uniform">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="$imgPathInsee"/>
                                </xsl:attribute>
                            </fo:external-graphic> 
                        </fo:block>
                    </fo:block-container>
                    
                    <fo:block-container absolute-position="absolute" left="72mm" top="0mm" width="26mm"
                        height="15mm">
                        <fo:block>
                            <fo:external-graphic content-width="scale-to-fit" width="15mm"
                                scaling="uniform">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="$imgPathMariane"/>
                                </xsl:attribute>
                            </fo:external-graphic> 
                        </fo:block>
                    </fo:block-container>
                    
                    <fo:block-container absolute-position="absolute" left="160mm" top="0mm" width="10mm"
                        height="15mm">
                        <fo:block>
                            <fo:external-graphic content-width="scale-to-fit" width="15mm"
                                scaling="uniform">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="$imgPathStatPub"/>
                                </xsl:attribute>
                            </fo:external-graphic>
                        </fo:block>
                    </fo:block-container>
                    
                    <fo:block-container absolute-position="absolute" left="10mm" top="23mm" width="150mm"
                        height="7mm">
                        <fo:block font-family="Liberation Sans" font-size="16pt" font-weight="bold"
                            text-align="center">
                            <!-- <xsl:call-template name="titre"/>  -->
                            <xsl:value-of select="$campaignLabel"/>
                        </fo:block>
                    </fo:block-container>
                    
                    <!--    Bloc Consignes retour -->
                    
                    <fo:block-container absolute-position="absolute" left="0mm" top="62mm" width="160mm"
                        height="32mm" font-family="Liberation Sans" font-size="10pt" font-weight="normal"
                        text-align="left">
                        
                        <fo:block line-height="14pt"> 
                            
                            <xsl:call-template name="entete"/>
                            
                        </fo:block>
                        
                    </fo:block-container>
                    
                    <!--    Blocs a quoi servent vos réponses ?  -->
                    <fo:block-container absolute-position="absolute" left="0mm" top="80mm" width="15mm"
                        height="15mm">
                        <fo:block margin="2pt">
                            <fo:external-graphic  content-width="scale-to-fit" width="15mm"
                                scaling="uniform">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="$imgPathAQuoi"/>
                                </xsl:attribute>
                            </fo:external-graphic>
                        </fo:block>
                    </fo:block-container>
                    
                    <fo:block-container absolute-position="absolute" left="20mm" top="87mm" width="170mm"
                        height="30mm" font-family="Liberation Sans" font-size="12pt">
                        <fo:block line-height="10pt">
                            <fo:inline font-weight="bold"> A quoi servent vos réponses ?
                            </fo:inline> 
                        </fo:block>
                    </fo:block-container>
                    
                    <fo:block-container absolute-position="absolute" left="0mm" top="100mm" width="170mm"
                        height="40mm" font-family="Liberation Sans" font-size="10pt" >
                        
                        <!-- list start -->
                        <fo:list-block>
                            <!-- list item -->
                            <fo:list-item>
                                
                                <!-- insert a bullet -->
                                <fo:list-item-label start-indent="5mm">
                                    <fo:block>
                                        <fo:inline>&#8226;</fo:inline>
                                    </fo:block>
                                </fo:list-item-label>
                                <!-- list text -->
                                <fo:list-item-body start-indent="10mm">
                                    <fo:block>L’Insee est un organisme public chargé de collecter, traiter, analyser et diffuser l’information statistique à caractère économique, démographique ou social.</fo:block>
                                    
                                </fo:list-item-body>
                            </fo:list-item>
                            
                            <fo:list-item>
                                <!-- insert a bullet -->
                                <fo:list-item-label start-indent="5mm">
                                    <fo:block>
                                        <fo:inline>&#8226;</fo:inline>
                                    </fo:block>
                                </fo:list-item-label>
                                <!-- list text -->
                                <fo:list-item-body start-indent="10mm">
                                    <fo:block>Il coordonne le système statistique français et participe aux travaux menés par les organismes internationaux, notamment Eurostat, l’Office statistique de l'Union européenne.</fo:block>
                                </fo:list-item-body>
                            </fo:list-item>
                            
                            <fo:list-item>
                                <!-- insert a bullet -->
                                <fo:list-item-label start-indent="5mm">
                                    <fo:block>
                                        <fo:inline>&#8226;</fo:inline>
                                    </fo:block>
                                </fo:list-item-label>
                                <!-- list text -->
                                <fo:list-item-body start-indent="10mm">
                                    <fo:block>Pour remplir sa mission statistique en toute indépendance, il dispose d’une large autonomie vis-à-vis des autres administrations.</fo:block>
                                    
                                </fo:list-item-body>
                            </fo:list-item>
                            
                            <fo:list-item>
                                <!-- insert a bullet -->
                                <fo:list-item-label start-indent="5mm">
                                    <fo:block>
                                        <fo:inline>&#8226;</fo:inline>
                                    </fo:block>
                                </fo:list-item-label>
                                <!-- list text -->
                                <fo:list-item-body start-indent="10mm">
                                    <fo:block>Les enquêtes de l’Insee servent à alimenter des études économiques et sociales qui intéressent un large public.</fo:block>
                                </fo:list-item-body>
                            </fo:list-item>
                            
                        </fo:list-block>
                        
                        
                    </fo:block-container>
                    
                    
                    
                    
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
        
    </xsl:template>
    
    <xsl:template name="entete">
        <fo:table table-layout="fixed" width="100%">
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block  font-family="Liberation Sans" font-size="11pt">
                            <xsl:text>Questionnaire </xsl:text>
                            <xsl:choose>
                                <xsl:when
                                    test="$date!=''">
                                    <xsl:text>expédié le </xsl:text>
                                    <xsl:value-of
                                        select="$date"
                                    />
                                </xsl:when>
                                <xsl:when
                                    test="$date=''">
                                    <xsl:text>non expédié</xsl:text>
                                </xsl:when>
                            </xsl:choose>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block  font-family="Liberation Sans" font-size="11pt" text-align="right">
                            <xsl:text>Identifiant : </xsl:text>
                            <xsl:value-of select="$unite"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <xsl:if
                    test="$date!=''">
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="2">
                            <fo:block font-family="Liberation Sans" font-size="11pt">
                                <xsl:text>Ce document vaut certificat de dépôt.</xsl:text>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:if>
            </fo:table-body>
        </fo:table>
    </xsl:template>
    
    <xsl:template name="titre">
        <xsl:value-of select="//xhtml:head/xhtml:title"/>
    </xsl:template>
    
    <!-- Par défaut on ne fait rien et on passe à en dessous -->
    <xsl:template match="*">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    
</xsl:stylesheet>
