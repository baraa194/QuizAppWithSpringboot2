package com.NTG.QuizAppStudentTask.Services;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {

    // مفتاح سري ثابت (لازم يبقى 32 بايت على الأقل مع HS256)
    private static final String SECRET_KEY = "u8Zqf9vC2n7W5Qz+1m4rA9h2o3vX6yPtJY7G2rF1vBc=";

    // بنرجع signing key جاهز للاستخدام
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ توليد التوكن مع إضافة الـ role
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // نفترض أن اليوزر ليه role واحد
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        claims.put("role", role); // ROLE_ADMIN or ROLE_TEACHER or ROLE_STUDENT

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // username
                .setIssuedAt(new Date(System.currentTimeMillis())) // تاريخ الإصدار
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // ساعة
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ استخراج الـ username من التوكن
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ استخراج الـ role من التوكن
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // استخراج claim معين
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ استرجاع كل الـ claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // التحقق من صلاحية التوكن
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // هل التوكن منتهي؟
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}

