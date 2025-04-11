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
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void shouldSaveStudentWithCourse() {
        // Arrange
        Course course = courseRepository.save(new Course(null, "Math", null));
        Student student = new Student(null, "samira", "samira@gmail.com", course);

        // Act
        Student savedStudent = studentRepository.save(student);

        // Assert
        assertNotNull(savedStudent.getId());
        assertEquals("samira", savedStudent.getName());
        assertEquals(course.getId(), savedStudent.getCourse().getId());
    }

    @Test
    void shouldFindStudentsByCourseId() {
        // Arrange
        Course mathCourse = courseRepository.save(new Course(null, "Math", null));
        Course physicsCourse = courseRepository.save(new Course(null, "Physics", null));

        studentRepository.save(new Student(null, "zahra", "zahra@gmail.com", mathCourse));
        studentRepository.save(new Student(null, "roz", "roz@gmail.com", mathCourse));
        studentRepository.save(new Student(null, "nazi", "nazi@gmail.com", physicsCourse));

        // Act
        List<Student> mathStudents = studentRepository.findByCourseId(mathCourse.getId());

        // Assert
        assertEquals(2, mathStudents.size());
        assertTrue(mathStudents.stream().allMatch(s -> s.getCourse().getId().equals(mathCourse.getId())));
    }

    @Test
    void shouldReturnAllStudentsWithPagination() {
        // Arrange
        Course course = courseRepository.save(new Course(null, "Math", null));
        studentRepository.save(new Student(null, "zahra", "zahra@gmail.com", course));
        studentRepository.save(new Student(null, "roz", "roz@gmail.com", course));
        studentRepository.save(new Student(null, "nazi", "nazi@gmail.com", course));

        Pageable pageable = PageRequest.of(0, 2);

        // Act
        Page<Student> studentsPage = studentRepository.findAll(pageable);

        // Assert
        assertEquals(3, studentsPage.getTotalElements());
        assertEquals(2, studentsPage.getContent().size());
    }
}