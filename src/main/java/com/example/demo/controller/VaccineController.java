package com.example.demo.controller;


//============================================================
//WHAT IS THIS FILE?
//The Controller handles all incoming HTTP requests.
//
//HTTP METHODS (the verbs of the web):
//GET    → read data        → @GetMapping
//POST   → create new data  → @PostMapping
//PUT    → replace data     → @PutMapping
//DELETE → remove data      → @DeleteMapping
//
//URL STRUCTURE:
///api/vaccines           ← base path (set on the class)
///api/vaccines/all       ← get all records
///api/vaccines/1         ← get record with id=1
///api/vaccines/country?name=India  ← filter by country
//
//RESPONSE FORMAT:
//Everything returns ResponseEntity<> which wraps your data
//with an HTTP status code:
//200 OK        → success
//201 CREATED   → successfully created
//404 NOT FOUND → record doesn't exist
//400 BAD REQUEST → invalid input
//============================================================

import com.example.demo.model.Vaccine;
import com.example.demo.service.VaccineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController                         // Combines @Controller + @ResponseBody
                                     // Every method automatically returns JSON
@RequestMapping("/api/vaccines")        // All URLs in this class start with /api/vaccines
@CrossOrigin(origins = "*")             // CRITICAL: allows your HTML frontend to call this API
                                     // Without this, browsers block the request (CORS policy)
public class VaccineController {

 @Autowired
 private VaccineService vaccineService;

 // ============================================================
 // 1. GET ALL VACCINES
 // URL: GET /api/vaccines/all
 // Returns: JSON array of all vaccine records
 // ============================================================
 @GetMapping("/all")
 public ResponseEntity<List<Vaccine>> getAllVaccines() {
     List<Vaccine> vaccines = vaccineService.getAllVaccines();
     return ResponseEntity.ok(vaccines);
     // ResponseEntity.ok() = HTTP 200 OK + the data
 }

 // ============================================================
 // 2. GET VACCINE BY ID
 // URL: GET /api/vaccines/1  (where 1 is the id)
 // {id} in the URL is a "path variable" — it changes per request
 // ============================================================
 @GetMapping("/{id}")
 public ResponseEntity<?> getVaccineById(@PathVariable Long id) {
     // @PathVariable extracts the {id} from the URL
     Optional<Vaccine> vaccine = vaccineService.getVaccineById(id);

     if (vaccine.isPresent()) {
         return ResponseEntity.ok(vaccine.get());
     } else {
         // Return 404 with an error message
         return ResponseEntity.status(HttpStatus.NOT_FOUND)
                 .body(Map.of("error", "Vaccine record not found with id: " + id));
     }
 }

 // ============================================================
 // 3. GET VACCINES BY COUNTRY
 // URL: GET /api/vaccines/country?name=India
 // @RequestParam reads the ?name=India part of the URL
 // ============================================================
 @GetMapping("/country")
 public ResponseEntity<List<Vaccine>> getByCountry(
         @RequestParam(name = "name") String country) {
     List<Vaccine> vaccines = vaccineService.getByCountry(country);
     return ResponseEntity.ok(vaccines);
 }

 // ============================================================
 // 4. SEARCH VACCINES BY COUNTRY (partial match)
 // URL: GET /api/vaccines/search?q=ind
 // Returns records for "India", "Indonesia" etc.
 // ============================================================
 @GetMapping("/search")
 public ResponseEntity<List<Vaccine>> searchVaccines(
         @RequestParam(name = "q") String keyword) {
     List<Vaccine> vaccines = vaccineService.searchByCountry(keyword);
     return ResponseEntity.ok(vaccines);
 }

 // ============================================================
 // 5. ADD NEW VACCINE RECORD
 // URL: POST /api/vaccines/add
 // Body: JSON object with vaccine data
 //
 // @RequestBody tells Spring to read the JSON from the request body
 // and convert it to a Vaccine object automatically
 //
 // @Valid triggers the validation annotations in Vaccine.java
 // (@NotBlank, @Min etc.)
 // ============================================================
 @PostMapping("/add")
 public ResponseEntity<?> addVaccine(@Valid @RequestBody Vaccine vaccine) {
     try {
         Vaccine saved = vaccineService.addVaccine(vaccine);
         return ResponseEntity.status(HttpStatus.CREATED).body(saved);
         // HTTP 201 CREATED = successfully made a new resource
     } catch (Exception e) {
         return ResponseEntity.badRequest()
                 .body(Map.of("error", "Failed to save record: " + e.getMessage()));
     }
 }

 // ============================================================
 // 6. UPDATE EXISTING VACCINE RECORD
 // URL: PUT /api/vaccines/update/1
 // Body: JSON with the fields you want to update
 // Only the fields you send get updated — others stay the same
 // ============================================================
 @PutMapping("/update/{id}")
 public ResponseEntity<?> updateVaccine(
         @PathVariable Long id,
         @RequestBody Vaccine vaccine) {
     try {
         Vaccine updated = vaccineService.updateVaccine(id, vaccine);
         return ResponseEntity.ok(updated);
     } catch (RuntimeException e) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND)
                 .body(Map.of("error", e.getMessage()));
     }
 }

 // ============================================================
 // 7. DELETE VACCINE RECORD
 // URL: DELETE /api/vaccines/delete/1
 // ============================================================
 @DeleteMapping("/delete/{id}")
 public ResponseEntity<?> deleteVaccine(@PathVariable Long id) {
     try {
         vaccineService.deleteVaccine(id);
         return ResponseEntity.ok(Map.of("message", "Record deleted successfully", "id", id));
     } catch (RuntimeException e) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND)
                 .body(Map.of("error", e.getMessage()));
     }
 }

 // ============================================================
 // 8. GET STATISTICS
 // URL: GET /api/vaccines/stats
 // Returns KPI numbers for the dashboard cards
 // ============================================================
 @GetMapping("/stats")
 public ResponseEntity<Map<String, Object>> getStatistics() {
     return ResponseEntity.ok(vaccineService.getStatistics());
 }

 // ============================================================
 // 9. GET CHART DATA — vaccinations per country
 // URL: GET /api/vaccines/charts/by-country
 // ============================================================
 @GetMapping("/charts/by-country")
 public ResponseEntity<List<Map<String, Object>>> getChartByCountry() {
     return ResponseEntity.ok(vaccineService.getVaccinationsByCountry());
 }

 // ============================================================
 // 10. GET CHART DATA — doses by vaccine type
 // URL: GET /api/vaccines/charts/by-vaccine-type
 // ============================================================
 @GetMapping("/charts/by-vaccine-type")
 public ResponseEntity<List<Map<String, Object>>> getChartByVaccineType() {
     return ResponseEntity.ok(vaccineService.getDosesByVaccineType());
 }

 // ============================================================
 // 11. GET CHART DATA — trend over time
 // URL: GET /api/vaccines/charts/trend
 // URL: GET /api/vaccines/charts/trend?country=India
 // @RequestParam(required = false) means the param is OPTIONAL
 // ============================================================
 @GetMapping("/charts/trend")
 public ResponseEntity<List<Map<String, Object>>> getTrend(
         @RequestParam(required = false, defaultValue = "all") String country) {
     return ResponseEntity.ok(vaccineService.getDosesOverTime(country));
 }

 // ============================================================
 // 12. GET ALL COUNTRIES (for dropdown filters)
 // URL: GET /api/vaccines/countries
 // ============================================================
 @GetMapping("/countries")
 public ResponseEntity<List<String>> getAllCountries() {
     return ResponseEntity.ok(vaccineService.getAllCountries());
 }

 // ============================================================
 // 13. GET ALL VACCINE NAMES (for dropdown filters)
 // URL: GET /api/vaccines/vaccine-names
 // ============================================================
 @GetMapping("/vaccine-names")
 public ResponseEntity<List<String>> getVaccineNames() {
     return ResponseEntity.ok(vaccineService.getAllVaccineNames());
 }

 // ============================================================
 // 14. HEALTH CHECK
 // URL: GET /api/vaccines/health
 // Used by the frontend to check if backend is reachable
 // ============================================================
 @GetMapping("/health")
 public ResponseEntity<Map<String, String>> health() {
     return ResponseEntity.ok(Map.of(
         "status",  "UP",
         "message", "COVID Vaccine Analysis API is running"
     ));
 }
}
