package com.example.course_management_system.repository;

import com.example.course_management_system.entity.Course;
import com.example.course_management_system.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class StudentRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp(){
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }
        @Test
        void shouldSaveStudentWithCourse(){
            // Arrange
            Course course = courseRepository.save(Course.builder()
                            .name("Math")
                            .build());
            Student student = Student.builder()
                    .name("samira")
                    .email("samira@gmail.com")
                    .course(course)
                    .build();
            // Act
            Student savedStudent = studentRepository.save(student);
            // Assert
            assertNotNull(savedStudent.getId());
            assertEquals("samira", savedStudent.getName());
        }
    //List<Student> findByCourseId(Long courseId);
    @Test
    void shouldFindStudentsByCourseId() {
        // Arrange
        Course mathCourse = courseRepository.save(Course.builder()
                        .name("Math")
                        .build());
        studentRepository.save(Student.builder()
                        .name("zahra")
                        .email("zahra@gmail.com")
                        .course(mathCourse)
                        .build());
        studentRepository.save(Student.builder()
                        .name("roz")
                        .email("roz@gmail.com")
                        .course(mathCourse)
                        .build());
        // Act
        List<Student> mathStudents = studentRepository.findByCourseId(mathCourse.getId());
        // Assert
        assertEquals(2, mathStudents.size());
    }
    //Page<Student> findAll(Pageable pageable);
    @Test
    void shouldReturnAllStudentsWithPagination() {
        // Arrange
        Course course = courseRepository.save(Course.builder()
                        .name("Math")
                        .build());
        studentRepository.save(Student.builder()
                        .name("zahra")
                        .email("zahra@gmail.com")
                        .course(course)
                        .build());
        studentRepository.save(Student.builder()
                        .name("roz")
                        .email("roz@gmail.com")
                        .course(course)
                        .build());
        Pageable pageable = PageRequest.of(0, 2);
        // Act
        Page<Student> studentsPage = studentRepository.findAll(pageable);
        // Assert
        assertEquals(2, studentsPage.getTotalElements());
        assertEquals(2, studentsPage.getContent().size());
    }
    @Test
    void shouldNotSaveStudentWithNullName() {
        // Arrange
        Course course = courseRepository.save(Course.builder().name("Math").build());
        Student student = Student.builder()
                .name(null)
                .email("samira@gmail.com")
                .course(course)
                .build();
        // Act & Assert
        assertThrows(Exception.class, () -> studentRepository.save(student));
    }
    @Test
    void shouldNotSaveStudentWithoutCourse() {
        // Arrange
        Student student = Student.builder()
                .name("samira")
                .email("samira@gmail.com")
                .course(null)  // missing required course
                .build();
        // Act & Assert
        assertThrows(Exception.class, () -> studentRepository.save(student));
    }
    @Test
    void shouldNotSaveStudentWithNonExistentCourse() {
        // Arrange
        Course nonExistentCourse = Course.builder().id(999L).name("Non-existent").build();
        Student student = Student.builder()
                .name("samira")
                .email("samira@gmail.com")
                .course(nonExistentCourse)
                .build();
        // Act & Assert
        assertThrows(Exception.class, () -> studentRepository.save(student));
    }
    @Test
    void shouldNotSaveDuplicateEmails() {
        // Arrange
        Course course = courseRepository.save(Course.builder().name("Math").build());
        String duplicateEmail = "samira@gmail.com";
        studentRepository.save(Student.builder()
                .name("samira1")
                .email(duplicateEmail)
                .course(course)
                .build());
        Student student2 = Student.builder()
                .name("samira2")
                .email(duplicateEmail)
                .course(course)
                .build();
        // Act & Assert
        assertThrows(Exception.class, () -> studentRepository.save(student2));
    }
    @Test
    void findByCourseId_ShouldReturnEmptyListForNonExistentCourse() {
        // Arrange
        Long nonExistentCourseId = 999L;
        // Act
        List<Student> students = studentRepository.findByCourseId(nonExistentCourseId);
        // Assert
        assertTrue(students.isEmpty());
    }
    @Test
    void findByCourseId_ShouldReturnEmptyListForCourseWithNoStudents() {
        // Arrange
        Course course = courseRepository.save(Course.builder().name("Empty Course").build());
        // Act
        List<Student> students = studentRepository.findByCourseId(course.getId());
        // Assert
        assertTrue(students.isEmpty());
    }
    @Test
    void findAllWithPagination_ShouldReturnEmptyPageWhenNoStudentsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        // Act
        Page<Student> studentsPage = studentRepository.findAll(pageable);
        // Assert
        assertTrue(studentsPage.isEmpty());
        assertEquals(0, studentsPage.getTotalElements());
    }
    @Test
    void findAllWithPagination_ShouldReturnEmptyPageForOutOfBoundsPageNumber() {
        // Arrange
        Course course = courseRepository.save(Course.builder().name("Math").build());
        studentRepository.save(Student.builder()
                .name("samira")
                .email("samira@gmail.com")
                .course(course)
                .build());
        Pageable pageable = PageRequest.of(1, 10);  // page 1 when only 1 student exists
        // Act
        Page<Student> studentsPage = studentRepository.findAll(pageable);
        // Assert
        assertTrue(studentsPage.isEmpty());
        assertEquals(1, studentsPage.getTotalElements());
    }

}