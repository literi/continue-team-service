package plugin.team.coreserver.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {



    @Value("${jwt.secret:your-custom-secret-key-that-is-at-least-64-bytes-long-1234567890123456789012345678901234567890123456789012345678901234}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:28800000}")
    private long jwtExpirationMs;
    
    private SecretKey getSigningKey() {
        // 确保密钥足够长以满足HS512的要求（至少64字节）
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            // 如果密钥长度不足，扩展密钥
            StringBuilder extendedKey = new StringBuilder();
            while (extendedKey.length() < 64) {
                extendedKey.append(jwtSecret);
            }
            keyBytes = extendedKey.toString().substring(0, 64).getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(Long userId, String username, Long tenantId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tenantId", tenantId);
        claims.put("role", role);
        
        return createToken(claims, username);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public Claims getClaimsFromToken(String token) {
        try {
           return Jwts.parser() // 替代 parserBuilder()
                .verifyWith(getSigningKey()) // 替代 setSigningKey()
                .build()
                .parseSignedClaims(token) // 替代 parseClaimsJws()
                .getPayload(); // 替代 getBody()（0.12.x 中 getBody() 改名为 getPayload()）
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            return null;
        }
    }
    
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? ((Number) claims.get("userId")).longValue() : null;
    }
    
    public Long getTenantIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? ((Number) claims.get("tenantId")).longValue() : null;
    }
    
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get("role") : null;
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
