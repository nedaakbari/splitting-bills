package ir.splitwise.splitbills.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ir.splitwise.splitbills.entity.AppUser;
import org.springframework.stereotype.Service;

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
}
