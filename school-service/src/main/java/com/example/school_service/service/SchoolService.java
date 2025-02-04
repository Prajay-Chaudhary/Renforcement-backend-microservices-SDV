package com.example.school_service.service;

import com.example.school_service.entity.School;
import com.example.school_service.repository.SchoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchoolService {
    @Autowired
    private SchoolRepository schoolRepository;

    private static final Logger logger = LoggerFactory.getLogger(SchoolService.class);

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public Optional<School> getSchoolById(Long id) {
        return schoolRepository.findById(id);
    }

    public School createSchool(School school) {
        logger.info("Received School for creation: {}", school); // Log input

        School savedSchool = schoolRepository.save(school);
        logger.info("Saved School: {}", savedSchool); // Log saved object

        return savedSchool;
    }

    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }
}
