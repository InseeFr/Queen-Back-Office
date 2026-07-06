package fr.insee.queen.application.configuration.log;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;

@RequiredArgsConstructor
@Slf4j
public class InvalidTokenUserExtractor {
    private final String principalAttribute;
    /**
     * Extract user id from jwt
     * @param token raw jwt
     * @return user id or null if error
     */
    public String extractUserId(String token) {
        if(token == null) {
            return null;
        }

        try {
            SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            String userId = claims.getStringClaim(principalAttribute);
            return (userId != null && !userId.isBlank())
                    ? userId
                    : null;

        } catch (ParseException e) {
            log.warn("Error when parsing token : {}", e.getMessage());
            return null;
        }
    }
}


