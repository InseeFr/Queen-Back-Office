package fr.insee.queen.domain.depositproof.service;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.depositproof.gateway.DepositProofGeneration;
import fr.insee.queen.domain.depositproof.model.PdfDepositProof;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.model.InterrogationDepositProof;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
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
    private final InterrogationService interrogationService;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    private final DepositProofGeneration depositProofGeneration;

    @Override
    public PdfDepositProof generateDepositProof(String userId, String interrogationId) {
        InterrogationDepositProof interrogation = interrogationService.getInterrogationDepositProof(interrogationId);
        String campaignId = interrogation.campaign().getId();
        String campaignLabel = interrogation.campaign().getLabel();
        String date = "";

        if (interrogation.stateData() == null) {
            throw new EntityNotFoundException(String.format("State data for interrogation %s was not found", interrogationId));
        }

        if (Arrays.asList(StateDataType.EXTRACTED, StateDataType.VALIDATED).contains(interrogation.stateData().state())) {
            LocalDateTime stateDate =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(interrogation.stateData().date()),
                            TimeZone.getDefault().toZoneId());
            date = dateFormat.format(stateDate);
        }
        String filename = String.format("%s_%s.pdf", campaignId, userId);

        File depositProof = depositProofGeneration.generateDepositProof(date, campaignLabel, userId);
        return new PdfDepositProof(filename, depositProof);
    }
}
