package com.example.demo.model;

// ============================================================
// Vaccine.java — DATA MODEL (No JPA/SQL needed for Firestore)
// This is a plain Java class (POJO).
// Firestore stores it as a "document" in a "vaccines" collection.
// Each field = one field in the Firestore document.
// ============================================================

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Vaccine {

    private String id;          // Firestore auto-generated document ID

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Vaccine name is required")
    private String vaccineName;

    @Min(value = 0, message = "Cannot be negative")
    private Long totalVaccinated;

    @Min(value = 0, message = "Cannot be negative")
    private Long totalDoses;

    @Min(value = 0, message = "Cannot be negative")
    private Long fullyVaccinated;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String recordDate;  // Stored as String in Firestore (yyyy-MM-dd)

    private Long population;

    // Required for Firestore deserialization
    public Vaccine() {}

    public Vaccine(String country, String vaccineName, Long totalVaccinated,
                   Long totalDoses, Long fullyVaccinated,
                   String recordDate, Long population) {
        this.country         = country;
        this.vaccineName     = vaccineName;
        this.totalVaccinated = totalVaccinated;
        this.totalDoses      = totalDoses;
        this.fullyVaccinated = fullyVaccinated;
        this.recordDate      = recordDate;
        this.population      = population;
    }

    // Getters and Setters
    public String getId()                         { return id; }
    public void setId(String id)                  { this.id = id; }

    public String getCountry()                    { return country; }
    public void setCountry(String country)        { this.country = country; }

    public String getVaccineName()                { return vaccineName; }
    public void setVaccineName(String v)          { this.vaccineName = v; }

    public Long getTotalVaccinated()              { return totalVaccinated; }
    public void setTotalVaccinated(Long t)        { this.totalVaccinated = t; }

    public Long getTotalDoses()                   { return totalDoses; }
    public void setTotalDoses(Long t)             { this.totalDoses = t; }

    public Long getFullyVaccinated()              { return fullyVaccinated; }
    public void setFullyVaccinated(Long f)        { this.fullyVaccinated = f; }

    public String getRecordDate()                 { return recordDate; }
    public void setRecordDate(String d)           { this.recordDate = d; }

    public Long getPopulation()                   { return population; }
    public void setPopulation(Long p)             { this.population = p; }
}
