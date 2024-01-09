package fr.insee.queen.domain.depositproof.service;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.depositproof.gateway.DepositProofGeneration;
import fr.insee.queen.domain.depositproof.model.PdfDepositProof;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.depositproof.model.SurveyUnitDepositProof;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFDepositProofService implements DepositProofService {
    private final SurveyUnitService surveyUnitService;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    private final DepositProofGeneration depositProofGeneration;

    @Override
    public PdfDepositProof generateDepositProof(String userId, String surveyUnitId) {
        SurveyUnitDepositProof surveyUnit = surveyUnitService.getSurveyUnitDepositProof(surveyUnitId);
        String campaignId = surveyUnit.campaign().getId();
        String campaignLabel = surveyUnit.campaign().getLabel();
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

        File depositProof = depositProofGeneration.generateDepositProof(date, campaignLabel, userId);
        return new PdfDepositProof(filename, depositProof);
    }
}
