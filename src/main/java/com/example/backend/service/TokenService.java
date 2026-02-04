package com.example.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${app.token.secret}") // เป็นค่าที่ backend ต้องตั้งเองเปลี่ยนทุกๆ 3 เดือน
    private String secret;

    @Value("${app.token.issuer}") // คนสร้าง
    private String issuer;

    public String tokenize(User user) { //สร้างToken
        Algorithm algorithm = Algorithm.HMAC256(secret);

        String token = JWT.create()
                .withIssuer(issuer)
                .withClaim("principal", user.getId())
                .withClaim("role", "USER")
                .withExpiresAt(
                        Date.from(Instant.now().plus(30, ChronoUnit.MINUTES))
                )
                .sign(algorithm);

        return token;
    }

    public DecodedJWT verify (String token) { // verifyToken
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);

        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
