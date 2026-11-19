package com.ueb.katalog.service;

import com.ueb.katalog.domain.Instructor;
import com.ueb.katalog.repository.CourseRepository;
import com.ueb.katalog.repository.InstructorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Logjika e instruktorëve: controller-i thërret këtë klasë, kjo thërret repository-n
@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    public InstructorService(InstructorRepository instructorRepository, CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }

    public List<Instructor> findAll() {
        return instructorRepository.findAllByOrderByLastNameAsc();
    }

    // Nëse id nuk ekziston, Spring shfaq faqen error/404.html
    public Instructor findById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    // id == null: instruktor i ri; përndryshe: ndryshim, pa e numëruar veten
    public boolean isEmailTaken(String email, Long id) {
        if (id == null) {
            return instructorRepository.existsByEmailIgnoreCase(email);
        }
        return instructorRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    // Instruktori që ka kurse nuk fshihet (SPEC pjesa 4)
    public boolean hasCourses(Long id) {
        return courseRepository.existsByInstructorId(id);
    }

    public void delete(Long id) {
        Instructor instructor = findById(id);
        instructorRepository.delete(instructor);
    }
}
