package com.example.course_management_system.repository;

import com.example.course_management_system.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThat;
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
    // Page<Course> findAll(Pageable pageable);
    @Test
    void shouldReturnAllCoursesWithPagination() {
        // Arrange
        List<Course> courses = List.of(
                Course.builder().name("Math").build(),
                Course.builder().name("Physics").build());
        courseRepository.saveAll(courses);
        //0 = page , 2 = size
        Pageable pageable = PageRequest.of(0, 2);
        // Act
        Page<Course> coursesPage = courseRepository.findAll(pageable);
        // Assert
        assertEquals(2, coursesPage.getTotalElements());
    }
    @Test
    void shouldUpdateCourse() {
        // Arrange
        Course course = courseRepository.save(Course.builder()
                .name("Math")
                .build());
        course.setName("Math2");
        courseRepository.save(course);
        //act
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        //assert
        assertThat(updatedCourse.getName()).isEqualTo("Math2");
    }
    @Test
    void shouldDeleteCourseById() {
        // Arrange
        Course course = Course.builder().
                name("Math").
                build();
        Course savedCourse = courseRepository.save(course);
        // Act
        courseRepository.deleteById(savedCourse.getId());
        // Assert
        Optional<Course> deletedCourse = courseRepository.findById(savedCourse.getId());
        assertTrue(deletedCourse.isEmpty());
    }

    @Test
    void shouldNotSaveCourseWhenNameIsNull() {
        // Arrange
        Course course = Course.builder()
                .name(null)  // invalid null name
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> courseRepository.save(course));
    }

    @Test
    void shouldNotFindCourseByNonExistentId() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<Course> foundCourse = courseRepository.findById(nonExistentId);

        // Assert
        assertThat(foundCourse).isEmpty();
    }

    @Test
    void shouldReturnEmptyPageWhenNoCoursesExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Course> coursesPage = courseRepository.findAll(pageable);

        // Assert
        assertThat(coursesPage.getContent()).isEmpty();
        assertThat(coursesPage.getTotalElements()).isZero();
    }

    @Test
    void shouldReturnEmptyListForOutOfBoundsPage() {
        // Arrange
        courseRepository.save(Course.builder().name("Math").build());
        Pageable pageable = PageRequest.of(1, 10);  // page 1 when only 1 item exists

        // Act
        Page<Course> coursesPage = courseRepository.findAll(pageable);

        // Assert
        assertThat(coursesPage.getContent()).isEmpty();
        assertThat(coursesPage.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldNotUpdateNonExistentCourse() {
        // Arrange
        Long nonExistentId = 999L;
        Course nonExistentCourse = Course.builder()
                .id(nonExistentId)
                .name("NonExistent")
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> courseRepository.save(nonExistentCourse));
    }

    @Test
    void shouldNotDeleteNonExistentCourse() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        courseRepository.deleteById(nonExistentId);

        // Assert (verify no exception thrown for non-existent delete)
        assertThat(courseRepository.findById(nonExistentId)).isEmpty();
    }

    @Test
    void shouldNotSaveDuplicateCourseNames() {
        // Arrange
        String duplicateName = "Math";
        Course course1 = Course.builder().name(duplicateName).build();
        Course course2 = Course.builder().name(duplicateName).build();

        // Act
        courseRepository.save(course1);

        // Assert
        assertThrows(Exception.class, () -> courseRepository.save(course2));
    }

    @Test
    void shouldHandleVeryLongCourseNames() {
        // Arrange
        String longName = "A".repeat(500);  // assuming 255 is max in entity
        Course course = Course.builder().name(longName).build();

        // Act & Assert
        assertThrows(Exception.class, () -> courseRepository.save(course));
    }

    @Test
    void shouldNotSaveCourseWithInvalidId() {
        // Arrange
        Course course = Course.builder()
                .id(-1L)  // invalid ID
                .name("Invalid")
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> courseRepository.save(course));
    }
}
