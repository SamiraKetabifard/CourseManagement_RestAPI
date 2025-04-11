package com.example.course_management_system.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    //Bidirectional
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Student> students = new ArrayList<>();
}
