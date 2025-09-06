package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String gender;
    private String phone;
    private int roleId;
    private String userName;
    private String password;
}
