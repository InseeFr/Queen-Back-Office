package fr.insee.queen.api.depositproof.service.model;

import java.io.File;

public record PdfDepositProof(String filename, File depositProof) {
}
