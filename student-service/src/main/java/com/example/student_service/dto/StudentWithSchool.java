package com.example.student_service.dto;

import com.example.student_service.entity.Student;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentWithSchool {
    private Student student;
    private Object school;
}