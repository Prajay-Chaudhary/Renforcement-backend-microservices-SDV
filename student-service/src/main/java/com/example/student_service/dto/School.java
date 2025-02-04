package com.example.student_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {
    private Long id;
    private String name;
    private String address;
    private String directorName;
}