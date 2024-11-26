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

@Service
public class JwtService {
    private static final String SECRET_KEY = "key";

    public String generateToken(AppUser appUser) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(appUser.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date())//todo
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)), SignatureAlgorithm.ES256)
                .compact();

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

    private static SecretKey getSecretKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }
}
