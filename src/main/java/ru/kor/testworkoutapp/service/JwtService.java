package ru.kor.testworkoutapp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
//    @Value("${jwt.secret-key}")
    private final static String SECRET_KEY = "FfMWrDzMe1wpqnRvr1Y0UEt1wH9akoZV7OgVJBAZeBuxW8bsYnD2qIltulMlQZRK3YqB9UcZ5HCzuFs324IYGYIDW/gurZp+QF9dTIn1wOmM6n18xNSOOVcZ5YoqXOd+Ekzn4B62hiPVVkJRHKwXBkIuvr9hUJNK4iI6u8lvP35SWnx/YtO9e25FGd66HBIxhbETZDaFcVA6CJoHQkH4S3Awoh3TIhJLK8dZEGD2H/Vqn3YcUE1Jsz/kAezV64Ayco7io4zZ3O9DTazmEcUeJvBafz9StgwcCW8S2w2/pDva45dJ1c0VzP+c8+wFuIRkWl3Pj6xEJ7yydqp1NpwTrDWqY8JuymbRdCIw3ZnZJKg=";

    private final static Date EXPIRATION_DATE = new Date(System.currentTimeMillis() + 1000 * 60 * 24); // 24 hours
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
//        return Jwts
//                .builder()
//                .claims()
//                .add(extraClaims)
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
//                .add(getSigningKey(), SignatureAlgorithm.HS256)
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(EXPIRATION_DATE)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
