package fr.insee.queen.domain.depositproof.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PDFDepositProofServiceTest {

    @Test
    void shouldDecodeValidBase64() {
        // given
        String original = "ABC_123-xyz";
        String encoded = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(original.getBytes(StandardCharsets.UTF_8));

        // when
        String decoded = PDFDepositProofService.decodeSurveyUnitCompositeName(encoded);

        // then
        assertThat(decoded).isEqualTo(original);
    }
    @Test
    void shouldThrowExceptionForNonBase64Input() {
        // given
        String invalid = "not_base64??!!";

        // then
        assertThatThrownBy(() -> PDFDepositProofService.decodeSurveyUnitCompositeName(invalid))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnEmptyStringWhenNull() {
        assertThat(PDFDepositProofService.decodeSurveyUnitCompositeName(null)).isEmpty();
    }

    @Test
    void shouldDecodeEmptyStringToEmptyString() {
        assertThat(PDFDepositProofService.decodeSurveyUnitCompositeName("")).isEmpty();
    }
}