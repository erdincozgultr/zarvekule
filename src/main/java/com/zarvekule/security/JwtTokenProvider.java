package com.zarvekule.security;

import com.zarvekule.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-ms}")
    private long jwtExpirationMs;

    public String generateToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        return generateToken(userPrincipal);
    }

    public String generateToken(User user) {
        String username = user.getUsername();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationMs);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser() // parser() yerine parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token); // parse() yerine parseClaimsJws()
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Geçersiz JWT Token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Süresi Dolmuş JWT Token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Desteklenmeyen JWT Token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claim dizisi boş: " + e.getMessage());
        }
        return false;
    }
}