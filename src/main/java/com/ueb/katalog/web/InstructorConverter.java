package com.ueb.katalog.web;

import com.ueb.katalog.domain.Instructor;
import com.ueb.katalog.repository.InstructorRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// Forma e kursit dërgon id-në e instruktorit nga <select>; kjo klasë e kthen në Instructor.
// Spring Boot e regjistron vetë çdo Converter që është @Component.
@Component
public class InstructorConverter implements Converter<String, Instructor> {

    private final InstructorRepository instructorRepository;

    public InstructorConverter(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    // Opsioni bosh, tekst që s'është numër ose id që s'ekziston -> null, që @NotNull të japë gabimin
    @Override
    public Instructor convert(String source) {
        if (source.isBlank()) {
            return null;
        }
        try {
            return instructorRepository.findById(Long.valueOf(source.trim())).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
