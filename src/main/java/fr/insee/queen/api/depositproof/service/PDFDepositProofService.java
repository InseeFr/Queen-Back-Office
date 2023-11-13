package fr.insee.queen.api.depositproof.service;

import fr.insee.queen.api.depositproof.service.exception.DepositProofException;
import fr.insee.queen.api.depositproof.service.generation.FoToPDFService;
import fr.insee.queen.api.depositproof.service.generation.GenerateFoService;
import fr.insee.queen.api.depositproof.service.model.PdfDepositProof;
import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.depositproof.service.model.SurveyUnitDepositProof;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFDepositProofService implements DepositProofService {

    private final GenerateFoService generateFoService;
    private final FoToPDFService foToPDFService;
    private final SurveyUnitService surveyUnitService;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    @Override
    public PdfDepositProof generateDepositProof(String userId, String surveyUnitId) {
        SurveyUnitDepositProof surveyUnit = surveyUnitService.getSurveyUnitDepositProof(surveyUnitId);
        String campaignId = surveyUnit.campaign().id();
        String campaignLabel = surveyUnit.campaign().label();
        String date = "";

        if (surveyUnit.stateData() == null) {
            throw new EntityNotFoundException(String.format("State data for survey unit %s was not found", surveyUnitId));
        }

        if (Arrays.asList(StateDataType.EXTRACTED, StateDataType.VALIDATED).contains(surveyUnit.stateData().state())) {
            LocalDateTime stateDate =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(surveyUnit.stateData().date()),
                            TimeZone.getDefault().toZoneId());
            date = dateFormat.format(stateDate);
        }
        String filename = String.format("%s_%s.pdf", campaignId, userId);

        return new PdfDepositProof(filename, retrievePdf(date, campaignLabel, userId));
    }

    private File retrievePdf(String date, String campaignLabel, String userId) {
        File pdfFile;
        try {
            File foFile = generateFoService.generateFo(date, campaignLabel, userId);
            pdfFile = foToPDFService.transformFoToPdf(foFile);
            Files.delete(foFile.toPath());
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new DepositProofException();
        }
        return pdfFile;
    }
}
