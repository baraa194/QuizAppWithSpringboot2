package com.NTG.QuizAppStudentTask.Services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import  com.NTG.QuizAppStudentTask.Repositories.roleRepo;
import  com.NTG.QuizAppStudentTask.Models.Role;
import  com.NTG.QuizAppStudentTask.DTO.RoleDTO;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final roleRepo RoleRepo;
    public Role addNewRole(RoleDTO roleDTO){
        Role role=new Role();
        role.setRole(roleDTO.getRole());
        RoleRepo.save(role);
        return role;
    }
}
