package com.NTG.QuizAppStudentTask.Controllers;

import com.NTG.QuizAppStudentTask.DTO.AuthRequest;
import com.NTG.QuizAppStudentTask.DTO.AuthResponse;
import com.NTG.QuizAppStudentTask.Services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Authenticate username + password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Load user details
            final UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
            if (user != null) {
                // Generate JWT token
                String token = jwtService.generateToken(user);

                // 🟢 استخرج الـ role (لو UserDetailsImpl عندك فيه roleId أو roleName)
                String role = user.getAuthorities()
                        .stream()
                        .findFirst()
                        .map(auth -> auth.getAuthority())
                        .orElse("UNKNOWN");

                return ResponseEntity.ok(new AuthResponse(token, role));
            }

            return ResponseEntity.status(400).body("User not found");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Authentication error: " + e.getMessage());
        }
    }


}