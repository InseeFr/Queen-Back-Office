package fr.insee.queen.domain.depositproof.gateway;

import java.io.File;

public interface DepositProofGeneration {
    File generateDepositProof(String date, String campaignLabel, String userId);
}
