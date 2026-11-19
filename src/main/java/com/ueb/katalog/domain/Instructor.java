package com.ueb.katalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

// Instruktori që jep kurse (tabela "instructor" në databazë)
@Entity
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Emri është i detyrueshëm.")
    @Size(max = 50, message = "Emri mund të ketë së shumti 50 shkronja.")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Mbiemri është i detyrueshëm.")
    @Size(max = 50, message = "Mbiemri mund të ketë së shumti 50 shkronja.")
    @Column(nullable = false, length = 50)
    private String lastName;

    // unique = true: databaza nuk lejon dy instruktorë me të njëjtin e-mail
    @NotBlank(message = "E-maili është i detyrueshëm.")
    @Email(message = "E-maili nuk është i vlefshëm.")
    @Size(max = 100, message = "E-maili mund të ketë së shumti 100 shkronja.")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Opsionale: mund të mbetet bosh
    @Size(max = 100, message = "Ekspertiza mund të ketë së shumti 100 shkronja.")
    @Column(length = 100)
    private String expertise;

    // Ana tjetër e relacionit: vetëm për lexim (p.sh. numri i kurseve në listë)
    @OneToMany(mappedBy = "instructor")
    private List<Course> courses = new ArrayList<>();

    // JPA kërkon një konstruktor pa parametra
    public Instructor() {
    }

    public Instructor(String firstName, String lastName, String email, String expertise) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.expertise = expertise;
    }

    // Emri i plotë, p.sh. "Arta Krasniqi"
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public List<Course> getCourses() {
        return courses;
    }
}
