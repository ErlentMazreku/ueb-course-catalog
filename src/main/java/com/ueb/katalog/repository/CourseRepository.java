package com.ueb.katalog.repository;

import com.ueb.katalog.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data e shkruan vetë SQL-in nga emrat e metodave
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Të gjitha kurset, sipas datës së fillimit (më i afërti i pari)
    List<Course> findAllByOrderByStartDateAscTitleAsc();

    // A ka ky instruktor së paku një kurs? (kontrolli para fshirjes)
    boolean existsByInstructorId(Long instructorId);
}
