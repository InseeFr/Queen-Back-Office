package fr.insee.queen.domain.depositproof.service;

import fr.insee.queen.domain.depositproof.model.PdfDepositProof;

public interface DepositProofService {
    PdfDepositProof generateDepositProof(String userId, String surveyUnitId);
}
