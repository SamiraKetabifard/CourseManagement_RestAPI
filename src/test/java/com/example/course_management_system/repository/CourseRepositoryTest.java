package com.example.course_management_system.repository;

import com.example.course_management_system.entity.Course;
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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CourseRepositoryTest {

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
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        // Clear data before each test
        courseRepository.deleteAll();
    }
    @Test
    void shouldSaveCourse() {
        // Arrange
        Course course = Course.builder()
                .name("Math")
                .build();
        // Act
        Course savedCourse = courseRepository.save(course);
        // Assert
        assertNotNull(savedCourse.getId());
        assertEquals("Math", savedCourse.getName());
    }
    @Test
    void shouldFindCourseById() {
        // Arrange
        Course course= Course.builder().
                name("Physics").
                build();
        Course savedCourse = courseRepository.save(course);
        // Act
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElse(null);
        // Assert
        assertNotNull(foundCourse);
        assertEquals(savedCourse.getId(), foundCourse.getId());
        assertEquals("Physics", foundCourse.getName());
    }
    @Test
    void shouldReturnAllCoursesWithPagination() {
        // Arrange
        courseRepository.save(new Course(null, "Math", null));
        courseRepository.save(new Course(null, "Physics", null));
        courseRepository.save(new Course(null, "Chemistry", null));

        Pageable pageable = PageRequest.of(0, 2);
        // Act
        Page<Course> coursesPage = courseRepository.findAll(pageable);
        // Assert
        assertEquals(3, coursesPage.getTotalElements());
        assertEquals(2, coursesPage.getContent().size());
    }
    @Test
    void shouldUpdateCourse() {
        // Arrange
        Course course = Course.builder()
                .name("Math")
                .build();
        Course savedCourse = courseRepository.save(course);
        // Act
        savedCourse.setName("Math2");
        Course updatedCourse = courseRepository.save(savedCourse);
        // Assert
        assertNotNull(updatedCourse);
        assertEquals("Math2", updatedCourse.getName());
    }
    @Test
    void shouldDeleteCourseById() {
        // Arrange
        Course course = Course.builder()
                .name("Math")
                .build();
        Course savedCourse = courseRepository.save(course);
        // Act
        courseRepository.deleteById(savedCourse.getId());
        // Assert
        Optional<Course> deletedCourse = courseRepository.findById(savedCourse.getId());
        assertTrue(deletedCourse.isEmpty());
    }
}