package com.NTG.QuizAppStudentTask.Models;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
        isDeleted = false;

        if (createdBy == null) {
            createdBy = getCurrentUserId();
        }
    }

    private Integer getCurrentUserId() {
        return 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (createdBy == null) {
            createdBy = getCurrentUserId();
        }
    }
}
