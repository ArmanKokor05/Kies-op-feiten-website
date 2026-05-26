package com.sop.backend.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import com.sop.backend.models.User;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * <a href="https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac">
 * Documentation JWT with HMAC protection</a>
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JSON Web Token (JWT) for a given user.
     *
     * @param user User object of the user for whom the token is generated
     * @return a signed JWT in compact string format
     * @throws JOSEException if an error occurs while signing the token
     */
    public String generateToken(User user) throws JOSEException {
         JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                 .subject(user.getId().toString())
                 .claim("email", user.getEmail())
                 .claim("name", user.getName())
                 .issueTime(new Date())
                 .expirationTime(new Date(System.currentTimeMillis() + expirationMs))
                 .jwtID(UUID.randomUUID().toString())
                 .build();

         SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

         signedJWT.sign(new MACSigner(secret.getBytes(StandardCharsets.UTF_8)));
         return signedJWT.serialize();
    }

    /**
     * Validates a JSON Web Token (JWT) and returns its subject.
     * @param authHeader the authentication header
     * @return the subject (user id) contained in the token
     * @throws Exception if the token is invalid or has expired
     */
    public String validateToken(String authHeader) throws Exception {
        if (authHeader == null || authHeader.isEmpty() || authHeader.equals("Bearer null")) {
            throw new BadCredentialsException("Not logged in");
        }

        String token = authHeader.substring(7);

        SignedJWT signedJWT = SignedJWT.parse(token);
        boolean tokenIsValid = signedJWT.verify(new MACVerifier(secret.getBytes(StandardCharsets.UTF_8)));
        if (!tokenIsValid) {
                throw new BadCredentialsException("Authorization token is not valid");
        }

        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (new Date().after(expiration)) {
                throw new BadCredentialsException("Login session has expired");
        }

        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
