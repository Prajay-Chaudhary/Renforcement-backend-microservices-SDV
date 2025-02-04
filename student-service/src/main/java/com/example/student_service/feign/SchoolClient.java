package com.example.student_service.feign;

import com.example.student_service.dto.School;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 🔥 Feign Client - Calls SCHOOL-SERVICE registered in Eureka
@FeignClient(name = "SCHOOL-SERVICE")
public interface SchoolClient {

    // Maps to School Service's GET /api/schools/{id}
    @GetMapping("/api/schools/{id}")
    School getSchoolById(@PathVariable("id") Long id);
}
