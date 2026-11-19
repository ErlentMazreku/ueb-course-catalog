package com.ueb.katalog.service;

import com.ueb.katalog.domain.Course;
import com.ueb.katalog.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Logjika e kurseve: controller-i thërret këtë klasë, kjo thërret repository-n
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAllByOrderByStartDateAscTitleAsc();
    }

    // Nëse id nuk ekziston, Spring shfaq faqen error/404.html
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        Course course = findById(id);
        courseRepository.delete(course);
    }
}
