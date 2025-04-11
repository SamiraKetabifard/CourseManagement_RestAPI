package com.example.course_management_system.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.course_management_system.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAll(Pageable pageable);
}
