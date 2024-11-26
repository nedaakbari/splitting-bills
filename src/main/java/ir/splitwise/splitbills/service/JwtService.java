package ir.splitwise.splitbills.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ir.splitwise.splitbills.entity.AppUser;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Service
public class JwtService {
    private static final String SECRET_KEY = "aJsi1dXkGVV/nHt5dmjkl1hXc2Plw3aW6rz5M+73d1I=";

    public String generateToken(AppUser appUser) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(appUser.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date())//todo
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)), SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean isTokenValid(String token, String userUsername) {
        boolean isTokenExpired = isTokenExpired(token);
        String subject = (String) getClaim(token, "subject");
        return isTokenExpired && Objects.equals(userUsername, subject);
    }


    private boolean isTokenExpired(String token) {
        Date iat = (Date) getClaim(token, "iat");
        return iat.before(new Date());
    }

    public Object getClaim(String token, String name) {
        SecretKey secretKey = getSecretKey();


        Claims claim = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody();
        return claim.get(name);
    }

    private SecretKey getSecretKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }
}
