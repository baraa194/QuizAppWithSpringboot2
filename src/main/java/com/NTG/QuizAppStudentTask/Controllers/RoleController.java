package com.NTG.QuizAppStudentTask.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.NTG.QuizAppStudentTask.Services.RoleService;
import com.NTG.QuizAppStudentTask.DTO.RoleDTO;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleService roleService ;

    @PostMapping("/addRole")
    public RoleDTO addRole(@RequestBody RoleDTO roleDTO){
        roleService.addNewRole(roleDTO);
        return roleDTO;
    }

}
