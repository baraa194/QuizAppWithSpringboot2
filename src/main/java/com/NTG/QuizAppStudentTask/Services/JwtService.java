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
import java.util.function.Function;
@Service
public class JwtService {

    // مفتاح سري ثابت (لازم يبقى 32 بايت على الأقل مع HS256)
    private static final String SECRET_KEY = "u8Zqf9vC2n7W5Qz+1m4rA9h2o3vX6yPtJY7G2rF1vBc=";

    // بنرجع signing key جاهز للاستخدام
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // توليد التوكن
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // بيحط الـ username
                .setIssuedAt(new Date(System.currentTimeMillis())) // تاريخ الإصدار
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // ساعة صلاحية
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // التوقيع بالمفتاح
                .compact();
    }

    // استخراج الـ username من التوكن
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // استخراج claim معين
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
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
