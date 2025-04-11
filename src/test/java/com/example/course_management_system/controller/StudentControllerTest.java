package com.example.course_management_system.controller;

import com.example.course_management_system.dto.StudentDTO;
import com.example.course_management_system.exception.ResourceNotFoundException;
import com.example.course_management_system.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private StudentService studentService;

    // Test data
    private final StudentDTO studentDTO = StudentDTO.builder()
            .id(1L)
            .name("samira")
            .email("samira@gmail.com")
            .courseId(1L)
            .build();

    @Test
    void createStudent_ShouldReturnCreated() throws Exception {
        // Arrange
        given(studentService.createStudent(any())).willReturn(studentDTO);

        // Act & Assert
        mockMvc.perform(post("/students/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("samira"))
                .andExpect(jsonPath("$.email").value("samira@gmail.com"));
    }

    @Test
    void getStudentById_ShouldReturnStudent() throws Exception {
        // Arrange
        given(studentService.getStudentById(1L)).willReturn(studentDTO);

        // Act & Assert
        mockMvc.perform(get("/students/get/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("samira"));
    }

    @Test
    void getAllStudents_ShouldReturnPage() throws Exception {
        // Arrange
        Page<StudentDTO> page = new PageImpl<>(List.of(studentDTO));
        given(studentService.getAllStudents(anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(page);

        // Act & Assert
        mockMvc.perform(get("/students/getall")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("samira"));
    }

    @Test
    void getStudentsByCourseId_ShouldReturnList() throws Exception {
        // Arrange
        given(studentService.getStudentsByCourseId(1L)).willReturn(List.of(studentDTO));

        // Act & Assert
        mockMvc.perform(get("/students/getcourse/{courseId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].courseId").value(1L));
    }

    @Test
    void updateStudent_ShouldReturnUpdatedStudent() throws Exception {
        // Arrange
        given(studentService.updateStudent(eq(1L), any())).willReturn(studentDTO);

        // Act & Assert
        mockMvc.perform(put("/students/edit/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("samira"));
    }

    @Test
    void deleteStudent_ShouldReturnNoContent() throws Exception {
        // Arrange
        willDoNothing().given(studentService).deleteStudent(1L);

        // Act & Assert
        mockMvc.perform(delete("/students/del/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string("Student deleted"));
    }

    @Test
    void getStudentById_WhenNotFound_ShouldReturn404() throws Exception {
        // Arrange
        given(studentService.getStudentById(99L))
                .willThrow(new ResourceNotFoundException("Student not found"));

        // Act & Assert
        mockMvc.perform(get("/students/get/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found"));
    }
}