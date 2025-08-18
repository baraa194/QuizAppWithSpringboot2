package com.NTG.QuizAppStudentTask.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String name;
    private String username;
    private String email;
    private String password;
    private int roleId;
}
