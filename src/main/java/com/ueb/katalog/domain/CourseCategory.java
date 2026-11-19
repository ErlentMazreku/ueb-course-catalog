package com.ueb.katalog.domain;

// Kategoritë e kurseve; në databazë ruhet emri (p.sh. "PROGRAMMING"), në faqe emri shqip
public enum CourseCategory {

    PROGRAMMING("Programim"),
    DESIGN("Dizajn"),
    BUSINESS("Biznes"),
    LANGUAGES("Gjuhë të huaja"),
    DATA("Shkenca e të dhënave");

    private final String displayName;

    CourseCategory(String displayName) {
        this.displayName = displayName;
    }

    // Emri që shfaqet në faqe
    public String getDisplayName() {
        return displayName;
    }
}
