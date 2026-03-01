package com.example.demo.repository;

//============================================================
//WHAT IS THIS FILE?
//This is the Repository — it talks to the database.
//
//MAGIC OF SPRING DATA JPA:
//You don't write any SQL here. Just by extending JpaRepository,
//Spring automatically gives you methods like:
//- findAll()         → SELECT * FROM vaccines
//- findById(id)      → SELECT * FROM vaccines WHERE id = ?
//- save(vaccine)     → INSERT or UPDATE
//- deleteById(id)    → DELETE FROM vaccines WHERE id = ?
//
//For custom queries, you either:
//1. Name your method in a special way (Spring reads the name!)
//2. Use @Query with JPQL (Java's version of SQL)
//============================================================

import com.example.demo.model.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository   // Marks this as a Spring-managed database component
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
 // JpaRepository<Vaccine, Long> means:
 //   Vaccine = the entity class this repo manages
 //   Long    = the type of the primary key (our id field)

 // --------------------------------------------------------
 // FIND BY COUNTRY
 // Spring reads the method name "findByCountry" and automatically
 // generates SQL: SELECT * FROM vaccines WHERE country = ?
 // No SQL needed — the method name IS the query!
 // --------------------------------------------------------
 List<Vaccine> findByCountry(String country);

 // --------------------------------------------------------
 // FIND BY COUNTRY (case-insensitive)
 // "IgnoreCase" makes it work for "india", "India", "INDIA"
 // --------------------------------------------------------
 List<Vaccine> findByCountryIgnoreCase(String country);

 // --------------------------------------------------------
 // FIND BY VACCINE NAME
 // --------------------------------------------------------
 List<Vaccine> findByVaccineNameIgnoreCase(String vaccineName);

 // --------------------------------------------------------
 // SEARCH COUNTRY (partial match — like a search bar)
 // "Containing" = SQL LIKE '%keyword%'
 // So searching "ind" finds "India", "Indonesia" etc.
 // --------------------------------------------------------
 List<Vaccine> findByCountryContainingIgnoreCase(String keyword);

 // --------------------------------------------------------
 // GET ALL DISTINCT COUNTRY NAMES
 // @Query uses JPQL (Java Persistence Query Language)
 // JPQL looks like SQL but uses CLASS names, not table names
 // "v.country" = the country field of the Vaccine class
 // --------------------------------------------------------
 @Query("SELECT DISTINCT v.country FROM Vaccine v ORDER BY v.country")
 List<String> findAllDistinctCountries();

 // --------------------------------------------------------
 // GET ALL DISTINCT VACCINE NAMES
 // --------------------------------------------------------
 @Query("SELECT DISTINCT v.vaccineName FROM Vaccine v ORDER BY v.vaccineName")
 List<String> findAllDistinctVaccineNames();

 // --------------------------------------------------------
 // TOTAL VACCINATED ACROSS ALL RECORDS
 // SUM() adds up all values. COALESCE handles nulls (returns 0 if null)
 // --------------------------------------------------------
 @Query("SELECT COALESCE(SUM(v.totalVaccinated), 0) FROM Vaccine v")
 Long sumTotalVaccinated();

 // --------------------------------------------------------
 // TOTAL DOSES ACROSS ALL RECORDS
 // --------------------------------------------------------
 @Query("SELECT COALESCE(SUM(v.totalDoses), 0) FROM Vaccine v")
 Long sumTotalDoses();

 // --------------------------------------------------------
 // TOTAL FULLY VACCINATED
 // --------------------------------------------------------
 @Query("SELECT COALESCE(SUM(v.fullyVaccinated), 0) FROM Vaccine v")
 Long sumFullyVaccinated();

 // --------------------------------------------------------
 // COUNT DISTINCT COUNTRIES
 // --------------------------------------------------------
 @Query("SELECT COUNT(DISTINCT v.country) FROM Vaccine v")
 Long countDistinctCountries();

 // --------------------------------------------------------
 // VACCINATIONS PER COUNTRY (for bar chart)
 // Returns an array of [countryName, totalVaccinated] pairs
 // Object[] = one row; List<Object[]> = all rows
 // --------------------------------------------------------
 @Query("SELECT v.country, SUM(v.totalVaccinated) FROM Vaccine v GROUP BY v.country ORDER BY SUM(v.totalVaccinated) DESC")
 List<Object[]> getVaccinationsByCountry();

 // --------------------------------------------------------
 // DOSES PER VACCINE TYPE (for doughnut chart)
 // --------------------------------------------------------
 @Query("SELECT v.vaccineName, SUM(v.totalDoses) FROM Vaccine v GROUP BY v.vaccineName ORDER BY SUM(v.totalDoses) DESC")
 List<Object[]> getDosesByVaccineType();

 // --------------------------------------------------------
 // TREND OVER TIME (for line chart)
 // Groups by date and sums doses per day
 // --------------------------------------------------------
 @Query("SELECT v.recordDate, SUM(v.totalDoses) FROM Vaccine v WHERE v.recordDate IS NOT NULL GROUP BY v.recordDate ORDER BY v.recordDate ASC")
 List<Object[]> getDosesOverTime();

 // --------------------------------------------------------
 // FIND BY COUNTRY FOR TREND (filtered line chart)
 // @Param("country") connects the method parameter to :country in the query
 // --------------------------------------------------------
 @Query("SELECT v.recordDate, SUM(v.totalDoses) FROM Vaccine v WHERE LOWER(v.country) = LOWER(:country) AND v.recordDate IS NOT NULL GROUP BY v.recordDate ORDER BY v.recordDate ASC")
 List<Object[]> getDosesOverTimeByCountry(@Param("country") String country);
}

