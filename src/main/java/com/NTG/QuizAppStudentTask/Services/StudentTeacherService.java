package com.NTG.QuizAppStudentTask.Services;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Models.StudentTeacher;
import com.NTG.QuizAppStudentTask.Repositories.StudentTeacherRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import com.NTG.QuizAppStudentTask.DTO.StudentTeacherDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentTeacherService {

    private final StudentTeacherRepo connectionRepo;
    private final userRepo UserRepository;

    public StudentTeacherDTO addConnection(int studentId, int teacherId) {
        User student = UserRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        User teacher = UserRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        StudentTeacher connection = new StudentTeacher();
        connection.setStudent(student);
        connection.setTeacher(teacher);
        connectionRepo.save(connection);

        StudentTeacherDTO dto =new StudentTeacherDTO();
        dto.setStudentId(connection.getStudent().getId());
        dto.setStudentId(connection.getTeacher().getId());
        dto.setCreatedTime(connection.getAssignedAt());
        return dto;

    }

    public void deleteConnection(Long connectionId) {
        connectionRepo.deleteById(connectionId);
    }

    public List<StudentTeacherDTO> getConnectionsByStudent(int studentId) {
        return connectionRepo.findByStudent_Id(studentId)
                .stream()
                .map(conn -> new StudentTeacherDTO(
                        conn.getId(),
                        conn.getStudent().getId(),
                        conn.getTeacher().getId(),
                        conn.getAssignedAt()
                ))
                .toList();
    }

    public List<StudentTeacherDTO> getConnectionsByTeacher(int teacherId) {
        return connectionRepo.findByTeacher_Id(teacherId)
                .stream()
                .map(conn -> new StudentTeacherDTO(
                        conn.getId(),
                        conn.getStudent().getId(),
                        conn.getTeacher().getId(),
                        conn.getAssignedAt()
                ))
                .toList();
    }

    public List<StudentTeacherDTO> getAllConnections() {
        return connectionRepo.findAll()
                .stream()
                .map(conn -> new StudentTeacherDTO(
                        conn.getId(),
                        conn.getStudent().getId(),
                        conn.getTeacher().getId(),
                        conn.getAssignedAt()
                ))
                .toList();
    }

}
