package com.NTG.QuizAppStudentTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class QuizAppStudentTaskApplication {

	public static void main(String[] args){ SpringApplication.run(QuizAppStudentTaskApplication.class, args);


		/*PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		String rawPassword = "admin123";
		String encodedPassword = passwordEncoder.encode(rawPassword);

		System.out.println("Raw Password: " + rawPassword);
		System.out.println("Encoded Password: " + encodedPassword);


		boolean matches = passwordEncoder.matches("admin123", encodedPassword);
		System.out.println("Password matches: " + matches);*/
	}

}
