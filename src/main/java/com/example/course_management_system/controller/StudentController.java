package com.example.course_management_system.controller;

import com.example.course_management_system.dto.StudentDTO;
import com.example.course_management_system.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/create")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO studentDTO = studentService.getStudentById(id);
        return ResponseEntity.ok(studentDTO);
    }
    @GetMapping("/getall")
    public ResponseEntity<Page<StudentDTO>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<StudentDTO> students = studentService.getAllStudents(page, size, sortBy, sortDir);
        return ResponseEntity.ok(students);
    }
    @GetMapping("getcourse/{courseId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(studentService.getStudentsByCourseId(courseId));
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>("Student deleted",HttpStatus.NO_CONTENT);
    }
}
