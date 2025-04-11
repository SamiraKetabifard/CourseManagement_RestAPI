package com.example.course_management_system.service;
import com.example.course_management_system.dto.StudentDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {

    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO getStudentById(Long id);
    Page<StudentDTO> getAllStudents(int page, int size, String sortBy, String sortDir);
    List<StudentDTO> getStudentsByCourseId(Long courseId);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);

}
