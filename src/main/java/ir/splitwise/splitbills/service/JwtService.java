package ir.splitwise.splitbills.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "aJsi1dXkGVV/nHt5dmjkl1hXc2Plw3aW6rz5M+73d1I=";

    public String generateToken(UserDetails userDetails) {

        Date iat = new Date(System.currentTimeMillis());
        return Jwts.builder().setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(iat)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))//todo getFrom application.properties;
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String userUsername) {
        String username = extractUsername(token);
        return (username.equals(userUsername)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date date = extractClaim(token, Claims::getExpiration);
        return date.before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Use the decoded key
                .build()
                .parseClaimsJws(token) // Use parseClaimsJws instead of parseClaimsJwt
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
