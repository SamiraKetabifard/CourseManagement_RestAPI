package com.example.course_management_system.service;

import com.example.course_management_system.dto.StudentDTO;
import com.example.course_management_system.entity.Course;
import com.example.course_management_system.entity.Student;
import com.example.course_management_system.exception.ResourceNotFoundException;
import com.example.course_management_system.repository.CourseRepository;
import com.example.course_management_system.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Course course = courseRepository.findById(studentDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + studentDTO.getCourseId()));
        Student student = modelMapper.map(studentDTO, Student.class);
        student.setCourse(course);
        Student savedStudent = studentRepository.save(student);
        return modelMapper.map(savedStudent, StudentDTO.class);
    }
    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return modelMapper.map(student, StudentDTO.class);
    }
    @Override
    public Page<StudentDTO> getAllStudents(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> students = studentRepository.findAll(pageable);
        return students.map(student -> modelMapper.map(student, StudentDTO.class));
    }
    @Override
    public List<StudentDTO> getStudentsByCourseId(Long courseId) {
        List<Student> students = studentRepository.findByCourseId(courseId);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        Course course = courseRepository.findById(studentDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Course not found with id: " + studentDTO.getCourseId()));
        existingStudent.setName(studentDTO.getName());
        existingStudent.setEmail(studentDTO.getEmail());
        existingStudent.setCourse(course);
        Student updatedStudent = studentRepository.save(existingStudent);
        return modelMapper.map(updatedStudent, StudentDTO.class);
    }
    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }
}