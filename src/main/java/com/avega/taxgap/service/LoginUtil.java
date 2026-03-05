package com.avega.taxgap.service;

import com.avega.taxgap.entity.User;
import com.avega.taxgap.exception.AccessDeniedException;
import com.avega.taxgap.exception.UserRequestException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static java.security.KeyRep.Type.SECRET;

@Component
public class LoginUtil {

    private static final Integer expirationTime = 60*60;
    private static final String secret = "ThisIsMySecretKeyForGeneratingAccessTokenUsingThisForGeneratingTokenWeCanUse";
    Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

    public String generateToken(User user){
        long currentTime = System.currentTimeMillis();
        long expiryTime = currentTime+expirationTime*1000;
        Date current = new Date(currentTime);
        Date expiry = new Date(expiryTime);

        return Jwts.builder().setIssuer(user.getId().toString())
                .issuedAt(current)
                .expiration(expiry)
                .signWith(key,SignatureAlgorithm.HS512)
                .claim("name",user.getName())
                .claim("email",user.getEmail())
                .claim("city",user.getCity())
                .compact();
    }

    public void validate(String token) throws Exception {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            UserRequestException errorException = new UserRequestException("Token","Token is invalid");
            throw new AccessDeniedException("Token is invalid",errorException);
        }
    }
}
