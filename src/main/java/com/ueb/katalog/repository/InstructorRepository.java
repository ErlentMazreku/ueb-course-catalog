package com.ueb.katalog.repository;

import com.ueb.katalog.domain.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data e shkruan vetë SQL-in nga emrat e metodave
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    // Të gjithë instruktorët, të renditur sipas mbiemrit
    List<Instructor> findAllByOrderByLastNameAsc();

    // A ekziston ky e-mail (pa dallim shkronjash të mëdha e të vogla)?
    boolean existsByEmailIgnoreCase(String email);

    // E njëjta, por pa e numëruar instruktorin me këtë id (përdoret te ndryshimi)
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
