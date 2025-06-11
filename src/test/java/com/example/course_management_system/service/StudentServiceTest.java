package com.example.course_management_system.service;

import com.example.course_management_system.dto.StudentDTO;
import com.example.course_management_system.entity.Course;
import com.example.course_management_system.entity.Student;
import com.example.course_management_system.exception.ResourceNotFoundException;
import com.example.course_management_system.repository.CourseRepository;
import com.example.course_management_system.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private StudentServiceImpl studentService;

    private final Student student = Student.builder()
            .id(1L)
            .name("samira")
            .email("samira@gmail.com")
            .course(new Course(1L, "Math", null))
            .build();

    private final StudentDTO studentDTO = StudentDTO.builder()
            .id(1L)
            .name("samira")
            .email("samira@gmail.com")
            .courseId(1L)
            .build();

    @Test
    void createStudent_ShouldReturnStudentDTO() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(new Course(1L, "Math", null)));
        when(modelMapper.map(any(StudentDTO.class), eq(Student.class))).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(modelMapper.map(any(Student.class), eq(StudentDTO.class))).thenReturn(studentDTO);
        // Act
        StudentDTO result = studentService.createStudent(studentDTO);
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(studentRepository).save(any(Student.class));
    }
    @Test
    void getStudentById_ShouldReturnStudentDTO() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(modelMapper.map(any(Student.class), eq(StudentDTO.class))).thenReturn(studentDTO);
        // Act
        StudentDTO result = studentService.getStudentById(1L);
        // Assert
        assertNotNull(result);
        assertEquals("samira", result.getName());
    }
    @Test
    void getAllStudents_ShouldReturnPage(){
        // Arrange
        String sortBy = "name";
        String sortDir = "asc";
        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Student> page = new PageImpl<>(List.of(student));
        when(studentRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(Student.class), eq(StudentDTO.class))).thenReturn(studentDTO);
        // Act
        Page<StudentDTO> result = studentService.getAllStudents(0, 10, sortBy, sortDir);
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("samira", result.getContent().get(0).getName());
        verify(studentRepository).findAll(pageable);
    }
    @Test
    void getStudentsByCourseId_ShouldReturnList() {
        // Arrange
        when(studentRepository.findByCourseId(1L)).thenReturn(List.of(student));
        when(modelMapper.map(any(Student.class), eq(StudentDTO.class))).thenReturn(studentDTO);
        // Act
        List<StudentDTO> result = studentService.getStudentsByCourseId(1L);
        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
    @Test
    void updateStudent_ShouldReturnUpdatedStudent() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(new Course()));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(modelMapper.map(any(Student.class), eq(StudentDTO.class))).thenReturn(studentDTO);
        // Act
        StudentDTO result = studentService.updateStudent(1L, studentDTO);
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(studentRepository).save(any(Student.class));
    }
    @Test
    void deleteStudent_ShouldDeleteWhenExists() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        willDoNothing().given(studentRepository).delete(any(Student.class));
        // Act
        studentService.deleteStudent(1L);
        // Assert
        verify(studentRepository).delete(any(Student.class));
    }
    @Test
    void createStudent_WithNonExistentCourse_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                studentService.createStudent(StudentDTO.builder()
                        .courseId(99L)
                        .email("samira@gmail.com")
                        .name("name")
                        .build()));
    }
    @Test
    void getStudentById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                studentService.getStudentById(99L));
    }
    @Test
    void getStudentsByCourseId_WhenCourseHasNoStudents_ShouldReturnEmptyList() {
        // Arrange
        when(studentRepository.findByCourseId(99L)).thenReturn(List.of());
        // Act
        List<StudentDTO> result = studentService.getStudentsByCourseId(99L);
        // Assert
        assertTrue(result.isEmpty());
    }
    @Test
    void updateStudent_WithNonExistentStudent_ShouldThrowException() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                studentService.updateStudent(99L, studentDTO));
    }
    @Test
    void updateStudent_WithNonExistentCourse_ShouldThrowException() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        StudentDTO dtoWithInvalidCourse = StudentDTO.builder()
                .id(1L)
                .courseId(99L)
                .email("valid@email.com")
                .name("name")
                .build();
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                studentService.updateStudent(1L, dtoWithInvalidCourse));
    }
    @Test
    void deleteStudent_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                studentService.deleteStudent(99L));
    }
    @Test
    void whenDatabaseErrorOccurs_ShouldThrowAppropriateException() {
        // Arrange
        when(studentRepository.findById(1L)).thenThrow(new RuntimeException("Database connection failed"));
        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                studentService.getStudentById(1L));
    }
    @Test
    void whenModelMapperFails_ShouldThrowException() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(modelMapper.map(any(Student.class), eq(StudentDTO.class)))
                .thenThrow(new RuntimeException("Mapping failed"));
        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                studentService.getStudentById(1L));
    }
}