package com.example.course_management_system.service;

import com.example.course_management_system.dto.CourseDTO;
import com.example.course_management_system.entity.Course;
import com.example.course_management_system.exception.ResourceNotFoundException;
import com.example.course_management_system.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private final Course course = Course.builder()
            .id(1L)
            .name("Math")
            .build();

    private final CourseDTO courseDTO = CourseDTO.builder()
            .id(1L)
            .name("Math")
            .build();

    @Test
    void createCourse_ShouldReturnCourseDTO() {
        // Arrange
        when(modelMapper.map(any(CourseDTO.class), eq(Course.class))).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(modelMapper.map(any(Course.class), eq(CourseDTO.class))).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.createCourse(courseDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void getCourseById_ShouldReturnCourseDTO() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(modelMapper.map(any(Course.class), eq(CourseDTO.class))).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.getCourseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Math", result.getName());
    }

    @Test
    void getCourseById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                courseService.getCourseById(99L));
    }

    @Test
    void getAllCourses_ShouldReturnPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Course> page = new PageImpl<>(Collections.singletonList(course));

        when(courseRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(Course.class), eq(CourseDTO.class))).thenReturn(courseDTO);

        // Act
        Page<CourseDTO> result = courseService.getAllCourses(0, 10, "name", "asc");

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Math", result.getContent().get(0).getName());
    }

    @Test
    void updateCourse_ShouldReturnUpdatedCourse() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(modelMapper.map(any(Course.class), eq(CourseDTO.class))).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.updateCourse(1L, courseDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourse_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                courseService.updateCourse(99L, courseDTO));
    }

    @Test
    void deleteCourse_ShouldDeleteWhenExists() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        willDoNothing().given(courseRepository).delete(any(Course.class));

        // Act
        courseService.deleteCourse(1L);

        // Assert
        verify(courseRepository).delete(any(Course.class));
    }

    @Test
    void deleteCourse_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                courseService.deleteCourse(99L));
    }
}