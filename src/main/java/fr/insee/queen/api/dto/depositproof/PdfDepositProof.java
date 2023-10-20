package fr.insee.queen.api.dto.depositproof;

import java.io.File;

public record PdfDepositProof(String filename, File depositProof) {
}
