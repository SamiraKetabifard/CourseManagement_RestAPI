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
        given(studentService.createStudent(any(StudentDTO.class))).willReturn(studentDTO);
        // Act & Assert
        mockMvc.perform(post("/students/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("samira"))
                .andExpect(jsonPath("$.email").value("samira@gmail.com"));
    }
    //  Page <StudentDTO>getAllStudents(int page, int size, String sortBy, String sortDir);
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
    void getStudentById_WhenNotFound_ShouldReturn404() throws Exception {
        // Arrange
        given(studentService.getStudentById(2L))
                .willThrow(new ResourceNotFoundException("Student not found"));
        // Act & Assert
        mockMvc.perform(get("/students/get/{id}", 2L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found"));
    }
    //  List<StudentDTO> getStudentsByCourseId(Long courseId);
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
        given(studentService.updateStudent(eq(1L), any(StudentDTO.class))).willReturn(studentDTO);
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
    void getStudentById_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/get/{id}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getStudentsByCourseId_WhenCourseNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        given(studentService.getStudentsByCourseId(999L))
                .willThrow(new ResourceNotFoundException("Course not found"));
        // Act & Assert
        mockMvc.perform(get("/students/getcourse/{courseId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));
    }
    @Test
    void updateStudent_WhenStudentNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        given(studentService.updateStudent(eq(999L), any(StudentDTO.class)))
                .willThrow(new ResourceNotFoundException("Student not found"));
        // Act & Assert
        mockMvc.perform(put("/students/edit/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found"));
    }
    @Test
    void deleteStudent_WhenStudentNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        willThrow(new ResourceNotFoundException("Student not found"))
                .given(studentService).deleteStudent(999L);
        // Act & Assert
        mockMvc.perform(delete("/students/del/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found"));
    }
    @Test
    void createStudent_WithNullBody_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/students/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}