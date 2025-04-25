package com.example.course_management_system.controller;

import com.example.course_management_system.dto.CourseDTO;
import com.example.course_management_system.exception.ResourceNotFoundException;
import com.example.course_management_system.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//for controller layer
@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    private final CourseDTO courseDTO = CourseDTO.builder()
            .id(1L)
            .name("Math")
            .build();

    @Test
    void createCourse_ShouldReturnCreatedCourse() throws Exception {
        // Arrange
        given(courseService.createCourse(any(CourseDTO.class))).willReturn(courseDTO);
        // Act & Assert
        mockMvc.perform(post("/courses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Math"));
    }
    @Test
    void getCourseById_ShouldReturnCourse() throws Exception {
        // Arrange
        given(courseService.getCourseById(1L)).willReturn(courseDTO);
        // Act & Assert
        mockMvc.perform(get("/courses/get/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Math"));
    }
    @Test
    void getCourseById_WhenNotFound_ShouldReturn404() throws Exception {
        // Arrange
        given(courseService.getCourseById(2L))
                .willThrow(new ResourceNotFoundException("Course not found"));
        // Act & Assert
        mockMvc.perform(get("/courses/get/{id}", 2L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));
    }
    @Test
    void getAllCourses_ShouldReturnPagedResults() throws Exception {
        // Arrange
        Page<CourseDTO> page = new PageImpl<>(Collections.singletonList(courseDTO));
        given(courseService.getAllCourses(anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(page);

        // Act & Assert
        mockMvc.perform(get("/courses/getall")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Math"));
    }

    @Test
    void updateCourse_ShouldReturnUpdatedCourse() throws Exception {
        // Arrange
        given(courseService.updateCourse(eq(1L), any(CourseDTO.class))).willReturn(courseDTO);

        // Act & Assert
        mockMvc.perform(put("/courses/edit/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Math"));
    }
    @Test
    void updateCourse_WhenNotFound_ShouldReturn404() throws Exception {
        // Arrange
        given(courseService.updateCourse(eq(2L), any(CourseDTO.class)))
                .willThrow(new ResourceNotFoundException("Course not found"));
        // Act & Assert
        mockMvc.perform(put("/courses/edit/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));
    }
    @Test
    void deleteCourse_ShouldReturnNoContent() throws Exception {
        // Arrange
        willDoNothing().given(courseService).deleteCourse(1L);

        // Act & Assert
        mockMvc.perform(delete("/courses/del/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string("Course deleted"));
    }
    @Test
    void deleteCourse_WhenNotFound_ShouldReturn404() throws Exception {
        // Arrange
        willThrow(new ResourceNotFoundException("Course not found"))
                .given(courseService).deleteCourse(2L);
        // Act & Assert
        mockMvc.perform(delete("/courses/del/{id}", 2L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));
    }
}