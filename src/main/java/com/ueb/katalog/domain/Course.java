package com.ueb.katalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

// Kursi (entiteti kryesor, tabela "course" në databazë)
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titulli është i detyrueshëm.")
    @Size(min = 3, max = 100, message = "Titulli duhet të ketë nga 3 deri në 100 shkronja.")
    @Column(nullable = false, length = 100)
    private String title;

    // Opsionale: mund të mbetet bosh
    @Size(max = 1000, message = "Përshkrimi mund të ketë së shumti 1000 shkronja.")
    @Column(length = 1000)
    private String description;

    // EnumType.STRING: ruhet emri i kategorisë, jo numri i radhës
    @NotNull(message = "Zgjidhni kategorinë.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseCategory category;

    @NotNull(message = "Çmimi është i detyrueshëm.")
    @DecimalMin(value = "0.0", message = "Çmimi nuk mund të jetë negativ.")
    @Digits(integer = 6, fraction = 2, message = "Çmimi mund të ketë së shumti 6 shifra para presjes dhe 2 pas saj.")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Kapaciteti është i detyrueshëm.")
    @Min(value = 1, message = "Kapaciteti duhet të jetë së paku 1.")
    @Max(value = 500, message = "Kapaciteti mund të jetë së shumti 500.")
    @Column(nullable = false)
    private Integer capacity;

    // ISO.DATE: formati "2026-12-01", i njëjti që dërgon <input type="date">
    @NotNull(message = "Data e fillimit është e detyrueshme.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Data e mbarimit është e detyrueshme.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate endDate;

    // Çdo kurs ka saktësisht një instruktor; kolona instructor_id është në tabelën course
    @NotNull(message = "Zgjidhni instruktorin.")
    @ManyToOne(optional = false)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // JPA kërkon një konstruktor pa parametra
    public Course() {
    }

    public Course(String title, String description, CourseCategory category, BigDecimal price,
                  Integer capacity, LocalDate startDate, LocalDate endDate, Instructor instructor) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructor = instructor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CourseCategory getCategory() {
        return category;
    }

    public void setCategory(CourseCategory category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }
}
