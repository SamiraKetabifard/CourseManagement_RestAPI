package com.example.course_management_system.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CourseDTO {

    private Long id;
    private String name;
    private List<StudentDTO> students;
}