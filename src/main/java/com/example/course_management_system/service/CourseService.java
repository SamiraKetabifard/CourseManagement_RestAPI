package com.example.course_management_system.service;
import com.example.course_management_system.dto.CourseDTO;
import org.springframework.data.domain.Page;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO getCourseById(Long id);
    Page<CourseDTO> getAllCourses(int page, int size, String sortBy, String sortDir);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);
}
