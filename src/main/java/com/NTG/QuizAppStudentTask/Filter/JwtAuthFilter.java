package com.NTG.QuizAppStudentTask.Filter;
import com.NTG.QuizAppStudentTask.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("🌐 Request path: " + path);


        if (path.startsWith("/auth")) {
            System.out.println(" Skipping JWT check for auth endpoint");
            chain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");
        System.out.println("🔑 Authorization header: " + authHeader);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No token or invalid format");
            response.setStatus(401);
            response.getWriter().write("{\"error\": \"Please provide a valid token\"}");
            return;
        }

        try {

            String token = authHeader.substring(7);
            System.out.println("🎫 Token: " + token);


            String username = jwtService.extractUsername(token);
            System.out.println("👤 Username from token: " + username);


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("📋 User roles: " + userDetails.getAuthorities());


                if (jwtService.validateToken(token, userDetails)) {
                    System.out.println("✅ Token is valid");


                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("🎯 User authenticated successfully: " + username);

                } else {
                    System.out.println("❌ Token is invalid");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\": \"Invalid token\"}");
                    return;
                }
            }


            chain.doFilter(request, response);

        } catch (Exception e) {
            System.out.println("💥 Error in JWT filter: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(401);
            response.getWriter().write("{\"error\": \"Authentication failed: " + e.getMessage() + "\"}");
        }
    }
}
