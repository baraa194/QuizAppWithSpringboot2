package com.NTG.QuizAppStudentTask.Controllers;


import com.NTG.QuizAppStudentTask.DTO.StudentTeacherDTO;
import com.NTG.QuizAppStudentTask.Services.StudentTeacherService;
import com.NTG.QuizAppStudentTask.Models.StudentTeacher;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
//@PreAuthorize("ADMIN")
@RequestMapping("/connections")
@AllArgsConstructor
public class StudentTeacherController {

    private final StudentTeacherService connectionService;

    @PostMapping("/addConnection")
    public StudentTeacherDTO addConnection(@RequestParam int studentId, @RequestParam int teacherId) {
        return connectionService.addConnection(studentId, teacherId);
    }

    @DeleteMapping("/{id}")
    public void deleteConnection(@PathVariable Long id) {
        connectionService.deleteConnection(id);
    }

    @GetMapping("/student/{studentId}")
    public List<StudentTeacherDTO> getByStudent(@PathVariable int studentId) {
        return connectionService.getConnectionsByStudent(studentId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<StudentTeacherDTO> getByTeacher(@PathVariable int teacherId) {
        return connectionService.getConnectionsByTeacher(teacherId);
    }
    @GetMapping
    public List<StudentTeacherDTO> getAllConnections() {
        return connectionService.getAllConnections();
    }
}
