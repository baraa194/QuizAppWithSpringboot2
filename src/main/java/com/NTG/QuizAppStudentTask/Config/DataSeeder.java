package com.NTG.QuizAppStudentTask.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Models.Role;
import com.NTG.QuizAppStudentTask.Repositories.RoleRepo;


@Configuration
public class DataSeeder {

    private final userRepo UserRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(userRepo UserRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder) {
        this.UserRepo = UserRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
//    @Bean
    CommandLineRunner seedAdmin() {
        return args -> {
            if (UserRepo.findByUsername("admin").isEmpty()) {

                // 1️⃣ دور على Role ADMIN
                Role adminRole = roleRepo.findById(1)
                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found in DB"));

                // 2️⃣ اعمل Admin user جديد
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setName("Super Admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(adminRole);
                admin.setCreatedBy(0);
                // 3️⃣ خزّنه في الـ DB
                UserRepo.save(admin);

                System.out.println("✅ Admin user seeded!");
            }
        };
    }
}