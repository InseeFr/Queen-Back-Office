package fr.insee.queen.api.depositproof.service;

import fr.insee.queen.api.depositproof.service.model.PdfDepositProof;

public interface DepositProofService {
    PdfDepositProof generateDepositProof(String userId, String surveyUnitId);
}
