package com.example.demo.controller;

// ============================================================
// VaccineController.java — REST API ENDPOINTS
// Same endpoints as before — only ID type changed Long → String
// because Firestore uses string document IDs (not auto-increment numbers)
// ============================================================

import com.example.demo.model.Vaccine;
import com.example.demo.service.VaccineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/vaccines")
@CrossOrigin(origins = "*")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    // GET /api/vaccines/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllVaccines() {
        try {
            return ResponseEntity.ok(vaccineService.getAllVaccines());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            Optional<Vaccine> vaccine = vaccineService.getVaccineById(id);
            if (vaccine.isPresent()) return ResponseEntity.ok(vaccine.get());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Record not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/country?name=India
    @GetMapping("/country")
    public ResponseEntity<?> getByCountry(@RequestParam String name) {
        try {
            return ResponseEntity.ok(vaccineService.getByCountry(name));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/search?q=ind
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        try {
            return ResponseEntity.ok(vaccineService.searchByCountry(q));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/vaccines/add
    @PostMapping("/add")
    public ResponseEntity<?> addVaccine(@Valid @RequestBody Vaccine vaccine) {
        try {
            Vaccine saved = vaccineService.addVaccine(vaccine);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to save: " + e.getMessage()));
        }
    }

    // PUT /api/vaccines/update/{id}
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateVaccine(@PathVariable String id,
                                           @RequestBody Vaccine vaccine) {
        try {
            return ResponseEntity.ok(vaccineService.updateVaccine(id, vaccine));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/vaccines/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVaccine(@PathVariable String id) {
        try {
            vaccineService.deleteVaccine(id);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully", "id", id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/stats
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            return ResponseEntity.ok(vaccineService.getStatistics());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/charts/by-country
    @GetMapping("/charts/by-country")
    public ResponseEntity<?> chartByCountry() {
        try {
            return ResponseEntity.ok(vaccineService.getVaccinationsByCountry());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/charts/by-vaccine-type
    @GetMapping("/charts/by-vaccine-type")
    public ResponseEntity<?> chartByType() {
        try {
            return ResponseEntity.ok(vaccineService.getDosesByVaccineType());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/charts/trend?country=all
    @GetMapping("/charts/trend")
    public ResponseEntity<?> chartTrend(
            @RequestParam(required = false, defaultValue = "all") String country) {
        try {
            return ResponseEntity.ok(vaccineService.getDosesOverTime(country));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/countries
    @GetMapping("/countries")
    public ResponseEntity<?> getCountries() {
        try {
            return ResponseEntity.ok(vaccineService.getAllCountries());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/vaccines/health
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status",  "UP",
            "message", "COVID Vaccine API is running with Firebase Firestore"
        ));
    }
}
