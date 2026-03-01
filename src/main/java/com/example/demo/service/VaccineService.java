package com.example.demo.service;


//============================================================
//WHAT IS THIS FILE?
//This is the SERVICE layer — the "business logic" of your app.
//
//THE 3-LAYER ARCHITECTURE (important concept):
//
//Controller  →  receives HTTP requests, sends responses
//    ↓
//Service     →  processes data, applies business rules  ← YOU ARE HERE
//    ↓
//Repository  →  talks to the database
//
//WHY SEPARATE LAYERS?
//- If you change your database, only Repository changes
//- If business rules change, only Service changes
//- Controller stays clean — it just routes requests
//- Each layer is independently testable
//============================================================

import com.example.demo.model.Vaccine;
import com.example.demo.repository.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service   // Marks this as a Spring-managed service component
public class VaccineService {

 // --------------------------------------------------------
 // DEPENDENCY INJECTION
 // @Autowired tells Spring: "find the VaccineRepository bean
 // and inject it here automatically"
 // You never write: new VaccineRepository() — Spring does it
 // --------------------------------------------------------
 @Autowired
 private VaccineRepository vaccineRepository;

 // ============================================================
 // 1. GET ALL VACCINES
 // ============================================================
 public List<Vaccine> getAllVaccines() {
     // findAll() is inherited from JpaRepository
     // Returns every row in the vaccines table
     return vaccineRepository.findAll();
 }

 // ============================================================
 // 2. GET VACCINE BY ID
 // ============================================================
 public Optional<Vaccine> getVaccineById(Long id) {
     // Optional = the record might or might not exist
     // findById returns Optional<Vaccine> — caller checks if it's present
     return vaccineRepository.findById(id);
 }

 // ============================================================
 // 3. GET VACCINES BY COUNTRY
 // ============================================================
 public List<Vaccine> getByCountry(String country) {
     return vaccineRepository.findByCountryIgnoreCase(country);
 }

 // ============================================================
 // 4. SEARCH VACCINES (partial country name match)
 // ============================================================
 public List<Vaccine> searchByCountry(String keyword) {
     return vaccineRepository.findByCountryContainingIgnoreCase(keyword);
 }

 // ============================================================
 // 5. GET VACCINES BY VACCINE NAME
 // ============================================================
 public List<Vaccine> getByVaccineName(String name) {
     return vaccineRepository.findByVaccineNameIgnoreCase(name);
 }

 // ============================================================
 // 6. ADD NEW VACCINE RECORD
 // ============================================================
 public Vaccine addVaccine(Vaccine vaccine) {
     // save() does INSERT if the object has no id,
     // or UPDATE if the object already has an id
     return vaccineRepository.save(vaccine);
 }

 // ============================================================
 // 7. UPDATE EXISTING VACCINE RECORD
 // ============================================================
 public Vaccine updateVaccine(Long id, Vaccine updatedVaccine) {
     // First check if the record exists
     Optional<Vaccine> existing = vaccineRepository.findById(id);

     if (existing.isPresent()) {
         Vaccine vaccine = existing.get();

         // Update only the fields that were provided
         // This is called a "partial update" — only change what's needed
         if (updatedVaccine.getCountry()         != null) vaccine.setCountry(updatedVaccine.getCountry());
         if (updatedVaccine.getVaccineName()     != null) vaccine.setVaccineName(updatedVaccine.getVaccineName());
         if (updatedVaccine.getTotalVaccinated() != null) vaccine.setTotalVaccinated(updatedVaccine.getTotalVaccinated());
         if (updatedVaccine.getTotalDoses()      != null) vaccine.setTotalDoses(updatedVaccine.getTotalDoses());
         if (updatedVaccine.getFullyVaccinated() != null) vaccine.setFullyVaccinated(updatedVaccine.getFullyVaccinated());
         if (updatedVaccine.getRecordDate()      != null) vaccine.setRecordDate(updatedVaccine.getRecordDate());
         if (updatedVaccine.getPopulation()      != null) vaccine.setPopulation(updatedVaccine.getPopulation());

         return vaccineRepository.save(vaccine);   // save() = UPDATE since it has an id
     } else {
         // Throw an exception that the controller will catch
         throw new RuntimeException("Vaccine record not found with id: " + id);
     }
 }

 // ============================================================
 // 8. DELETE VACCINE RECORD
 // ============================================================
 public void deleteVaccine(Long id) {
     if (!vaccineRepository.existsById(id)) {
         throw new RuntimeException("Vaccine record not found with id: " + id);
     }
     vaccineRepository.deleteById(id);
 }

 // ============================================================
 // 9. GET STATISTICS (for KPI cards on dashboard)
 // Returns a Map — like a dictionary: key → value
 // This becomes a JSON object in the API response
 // ============================================================
 public Map<String, Object> getStatistics() {
     Map<String, Object> stats = new HashMap<>();

     // Basic counts and sums
     stats.put("totalRecords",       vaccineRepository.count());
     stats.put("totalVaccinated",    vaccineRepository.sumTotalVaccinated());
     stats.put("totalDoses",         vaccineRepository.sumTotalDoses());
     stats.put("fullyVaccinated",    vaccineRepository.sumFullyVaccinated());
     stats.put("countriesTracked",   vaccineRepository.countDistinctCountries());
     stats.put("vaccineTypes",       vaccineRepository.findAllDistinctVaccineNames().size());

     return stats;
     /*
     This returns JSON like:
     {
         "totalRecords": 150,
         "totalVaccinated": 5200000000,
         "totalDoses": 11500000000,
         "fullyVaccinated": 4800000000,
         "countriesTracked": 195,
         "vaccineTypes": 12
     }
     */
 }

 // ============================================================
 // 10. GET CHART DATA — vaccinations per country
 // ============================================================
 public List<Map<String, Object>> getVaccinationsByCountry() {
     List<Object[]> raw = vaccineRepository.getVaccinationsByCountry();
     List<Map<String, Object>> result = new ArrayList<>();

     for (Object[] row : raw) {
         // Each row is [countryName, totalVaccinated]
         Map<String, Object> item = new HashMap<>();
         item.put("country",  row[0]);   // index 0 = first SELECT field
         item.put("total",    row[1]);   // index 1 = second SELECT field
         result.add(item);
     }
     return result;
     /*
     Returns JSON like:
     [
       { "country": "India",  "total": 1800000000 },
       { "country": "China",  "total": 2400000000 },
       ...
     ]
     */
 }

 // ============================================================
 // 11. GET CHART DATA — doses by vaccine type
 // ============================================================
 public List<Map<String, Object>> getDosesByVaccineType() {
     List<Object[]> raw = vaccineRepository.getDosesByVaccineType();
     List<Map<String, Object>> result = new ArrayList<>();

     for (Object[] row : raw) {
         Map<String, Object> item = new HashMap<>();
         item.put("vaccineName", row[0]);
         item.put("doses",       row[1]);
         result.add(item);
     }
     return result;
 }

 // ============================================================
 // 12. GET CHART DATA — doses over time (trend)
 // ============================================================
 public List<Map<String, Object>> getDosesOverTime(String country) {
     List<Object[]> raw;

     if (country != null && !country.isBlank() && !country.equalsIgnoreCase("all")) {
         // Filtered by country
         raw = vaccineRepository.getDosesOverTimeByCountry(country);
     } else {
         // All countries
         raw = vaccineRepository.getDosesOverTime();
     }

     List<Map<String, Object>> result = new ArrayList<>();
     for (Object[] row : raw) {
         Map<String, Object> item = new HashMap<>();
         item.put("date",  row[0].toString());   // LocalDate → String
         item.put("doses", row[1]);
         result.add(item);
     }
     return result;
 }

 // ============================================================
 // 13. GET ALL DISTINCT COUNTRIES (for dropdown filter)
 // ============================================================
 public List<String> getAllCountries() {
     return vaccineRepository.findAllDistinctCountries();
 }

 // ============================================================
 // 14. GET ALL DISTINCT VACCINE NAMES (for dropdown filter)
 // ============================================================
 public List<String> getAllVaccineNames() {
     return vaccineRepository.findAllDistinctVaccineNames();
 }
}

