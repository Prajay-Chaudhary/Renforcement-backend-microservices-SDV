package com.example.student_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students") // Collection in MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student {
    @Id
    private String id;

    private String name;
    private String genre;
    private Long schoolId; // This references the School from the School Microservice
}