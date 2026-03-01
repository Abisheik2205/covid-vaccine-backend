package com.example.demo.model;

//============================================================
//WHAT IS THIS FILE?
//This is the "Model" or "Entity" class.
//It defines what a single vaccine record looks like.
//Spring Boot will automatically CREATE a database table
//called "vaccines" based on this class.
//
//Every field here = one column in the database table.
//============================================================

import jakarta.persistence.*;         // JPA annotations for database mapping
import jakarta.validation.constraints.*; // For input validation
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity                          // Tells Spring: "map this class to a database table"
@Table(name = "vaccines")        // The table will be named "vaccines"
public class Vaccine {

 // --------------------------------------------------------
 // PRIMARY KEY
 // Every database row needs a unique ID.
 // @GeneratedValue means the database auto-assigns it (1, 2, 3...)
 // --------------------------------------------------------
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 // --------------------------------------------------------
 // COUNTRY
 // @NotBlank = cannot be empty or null when saving
 // @Column(nullable = false) = database also enforces this
 // --------------------------------------------------------
 @NotBlank(message = "Country is required")
 @Column(nullable = false, length = 100)
 private String country;

 // --------------------------------------------------------
 // VACCINE NAME (e.g. "Pfizer-BioNTech", "Covaxin", "Moderna")
 // --------------------------------------------------------
 @NotBlank(message = "Vaccine name is required")
 @Column(name = "vaccine_name", nullable = false, length = 100)
 private String vaccineName;

 // --------------------------------------------------------
 // TOTAL PEOPLE WHO RECEIVED AT LEAST ONE DOSE
 // @Min(0) = cannot be negative
 // --------------------------------------------------------
 @Min(value = 0, message = "Total vaccinated cannot be negative")
 @Column(name = "total_vaccinated")
 private Long totalVaccinated;

 // --------------------------------------------------------
 // TOTAL DOSES ADMINISTERED (includes 2nd, 3rd doses etc.)
 // This is always >= totalVaccinated
 // --------------------------------------------------------
 @Min(value = 0, message = "Total doses cannot be negative")
 @Column(name = "total_doses")
 private Long totalDoses;

 // --------------------------------------------------------
 // FULLY VACCINATED (received ALL required doses)
 // --------------------------------------------------------
 @Min(value = 0, message = "Fully vaccinated cannot be negative")
 @Column(name = "fully_vaccinated")
 private Long fullyVaccinated;

 // --------------------------------------------------------
 // DATE OF THIS DATA RECORD
 // @JsonFormat tells Jackson (JSON library) how to format dates
 // --------------------------------------------------------
 @JsonFormat(pattern = "yyyy-MM-dd")
 @Column(name = "record_date")
 private LocalDate recordDate;

 // --------------------------------------------------------
 // POPULATION OF THE COUNTRY (for percentage calculations)
 // --------------------------------------------------------
 @Min(value = 0, message = "Population cannot be negative")
 @Column(name = "population")
 private Long population;

 // --------------------------------------------------------
 // CONSTRUCTORS
 // Java needs these to create objects.
 // The no-arg constructor is REQUIRED by Spring/JPA.
 // --------------------------------------------------------
 public Vaccine() {}   // required by JPA — do not remove!

 public Vaccine(String country, String vaccineName, Long totalVaccinated,
                Long totalDoses, Long fullyVaccinated, LocalDate recordDate, Long population) {
     this.country          = country;
     this.vaccineName      = vaccineName;
     this.totalVaccinated  = totalVaccinated;
     this.totalDoses       = totalDoses;
     this.fullyVaccinated  = fullyVaccinated;
     this.recordDate       = recordDate;
     this.population       = population;
 }

 // --------------------------------------------------------
 // GETTERS AND SETTERS
 // Spring needs these to read/write field values.
 // In Eclipse: right-click → Source → Generate Getters and Setters
 // --------------------------------------------------------
 public Long getId()                        { return id; }
 public void setId(Long id)                 { this.id = id; }

 public String getCountry()                 { return country; }
 public void setCountry(String country)     { this.country = country; }

 public String getVaccineName()             { return vaccineName; }
 public void setVaccineName(String v)       { this.vaccineName = v; }

 public Long getTotalVaccinated()           { return totalVaccinated; }
 public void setTotalVaccinated(Long t)     { this.totalVaccinated = t; }

 public Long getTotalDoses()                { return totalDoses; }
 public void setTotalDoses(Long t)          { this.totalDoses = t; }

 public Long getFullyVaccinated()           { return fullyVaccinated; }
 public void setFullyVaccinated(Long f)     { this.fullyVaccinated = f; }

 public LocalDate getRecordDate()           { return recordDate; }
 public void setRecordDate(LocalDate d)     { this.recordDate = d; }

 public Long getPopulation()                { return population; }
 public void setPopulation(Long p)          { this.population = p; }

 // toString() is useful for debugging — prints the object as text
 @Override
 public String toString() {
     return "Vaccine{id=" + id + ", country='" + country + "', vaccine='" + vaccineName + "'}";
 }
}

