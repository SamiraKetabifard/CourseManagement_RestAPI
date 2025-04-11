package com.example.course_management_system.dto;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class StudentDTO {

    private Long id;
    private String name;
    private String email;
    private Long courseId;
}