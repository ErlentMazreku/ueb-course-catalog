package com.ueb.katalog.config;

import com.ueb.katalog.domain.Course;
import com.ueb.katalog.domain.CourseCategory;
import com.ueb.katalog.domain.Instructor;
import com.ueb.katalog.repository.CourseRepository;
import com.ueb.katalog.repository.InstructorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Të dhëna testuese: shtohen vetëm kur databaza është bosh, që nisjet e tjera të mos i dyfishojnë
@Component
public class DataLoader implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    public DataLoader(InstructorRepository instructorRepository, CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (instructorRepository.count() > 0 || courseRepository.count() > 0) {
            return;
        }

        Instructor arta = new Instructor("Arta", "Krasniqi", "arta.krasniqi@example.com", "Java dhe Spring");
        Instructor driton = new Instructor("Driton", "Berisha", "driton.berisha@example.com", "Dizajn grafik dhe UX");
        Instructor blerta = new Instructor("Blerta", "Gashi", "blerta.gashi@example.com", "Analizë të dhënash dhe biznes");
        instructorRepository.saveAll(List.of(arta, driton, blerta));

        courseRepository.saveAll(List.of(
                new Course("Java për fillestarë",
                        "Bazat e programimit me Java: variablat, kushtet, ciklet, klasat dhe objektet.",
                        CourseCategory.PROGRAMMING, new BigDecimal("120.00"), 25,
                        LocalDate.of(2027, 1, 11), LocalDate.of(2027, 3, 26), arta),
                new Course("Aplikacione web me Spring Boot",
                        "Ndërtimi i një aplikacioni web me Spring MVC, Thymeleaf dhe PostgreSQL.",
                        CourseCategory.PROGRAMMING, new BigDecimal("180.00"), 20,
                        LocalDate.of(2027, 4, 5), LocalDate.of(2027, 6, 25), arta),
                new Course("Dizajni i ndërfaqeve (UI/UX)",
                        "Parimet e dizajnit, prototipet në Figma dhe testimi me përdorues.",
                        CourseCategory.DESIGN, new BigDecimal("150.00"), 18,
                        LocalDate.of(2027, 2, 1), LocalDate.of(2027, 4, 30), driton),
                new Course("Analiza e të dhënave me Python",
                        "Pastrimi dhe analiza e të dhënave me pandas, grafikët dhe raportet.",
                        CourseCategory.DATA, new BigDecimal("200.00"), 30,
                        LocalDate.of(2027, 3, 1), LocalDate.of(2027, 7, 30), blerta),
                new Course("Menaxhimi i projekteve",
                        null,
                        CourseCategory.BUSINESS, new BigDecimal("90.00"), 40,
                        LocalDate.of(2027, 1, 18), LocalDate.of(2027, 2, 26), blerta)
        ));
    }
}
