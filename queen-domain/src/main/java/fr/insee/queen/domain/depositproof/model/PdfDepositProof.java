package fr.insee.queen.domain.depositproof.model;

import java.io.File;

public record PdfDepositProof(String filename, File depositProof) {
}
