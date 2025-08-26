package com.NTG.QuizAppStudentTask.Config;

import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")

public class AuditorAwareImpl implements AuditorAware<String> {
    @Autowired
    private userRepo userRepository;

    @Override

    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return Optional.of(auth.getName()); // username
        }
        return Optional.empty();
    }

}