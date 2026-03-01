package com.example.demo.service;

// ============================================================
// VaccineService.java — FIRESTORE BUSINESS LOGIC
// Replaces JPA Repository with Firestore API calls.
// Firestore stores data as Documents inside Collections.
// Collection = "vaccines" (like a SQL table)
// Document   = one vaccine record (like a SQL row)
// ============================================================

import com.example.demo.model.Vaccine;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class VaccineService {

    private static final String COLLECTION = "vaccines";

    @Autowired
    private Firestore firestore;

    // -------------------------------------------------------
    // GET ALL VACCINES
    // -------------------------------------------------------
    public List<Vaccine> getAllVaccines() throws ExecutionException, InterruptedException {
        // Get all documents from "vaccines" collection
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        List<Vaccine> list = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            Vaccine v = doc.toObject(Vaccine.class);
            v.setId(doc.getId());  // set Firestore document ID
            list.add(v);
        }
        return list;
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------
    public Optional<Vaccine> getVaccineById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            Vaccine v = doc.toObject(Vaccine.class);
            v.setId(doc.getId());
            return Optional.of(v);
        }
        return Optional.empty();
    }

    // -------------------------------------------------------
    // SEARCH BY COUNTRY (partial match)
    // -------------------------------------------------------
    public List<Vaccine> searchByCountry(String keyword) throws ExecutionException, InterruptedException {
        List<Vaccine> all = getAllVaccines();
        String lower = keyword.toLowerCase();
        return all.stream()
            .filter(v -> v.getCountry() != null && v.getCountry().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET BY COUNTRY (exact match)
    // -------------------------------------------------------
    public List<Vaccine> getByCountry(String country) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
            .whereEqualTo("country", country).get();
        List<Vaccine> list = new ArrayList<>();
        for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
            Vaccine v = doc.toObject(Vaccine.class);
            v.setId(doc.getId());
            list.add(v);
        }
        return list;
    }

    // -------------------------------------------------------
    // ADD VACCINE
    // -------------------------------------------------------
    public Vaccine addVaccine(Vaccine vaccine) throws ExecutionException, InterruptedException {
        // Firestore auto-generates a unique document ID
        DocumentReference ref = firestore.collection(COLLECTION).document();
        vaccine.setId(ref.getId());

        // Convert to map to store in Firestore
        Map<String, Object> data = vaccineToMap(vaccine);
        ref.set(data).get();  // .get() waits for write to complete
        return vaccine;
    }

    // -------------------------------------------------------
    // UPDATE VACCINE
    // -------------------------------------------------------
    public Vaccine updateVaccine(String id, Vaccine updated)
            throws ExecutionException, InterruptedException {
        DocumentReference ref = firestore.collection(COLLECTION).document(id);
        DocumentSnapshot doc  = ref.get().get();

        if (!doc.exists()) throw new RuntimeException("Vaccine record not found with id: " + id);

        // Only update non-null fields
        Map<String, Object> updates = new HashMap<>();
        if (updated.getCountry()         != null) updates.put("country",         updated.getCountry());
        if (updated.getVaccineName()     != null) updates.put("vaccineName",     updated.getVaccineName());
        if (updated.getTotalVaccinated() != null) updates.put("totalVaccinated", updated.getTotalVaccinated());
        if (updated.getTotalDoses()      != null) updates.put("totalDoses",       updated.getTotalDoses());
        if (updated.getFullyVaccinated() != null) updates.put("fullyVaccinated", updated.getFullyVaccinated());
        if (updated.getRecordDate()      != null) updates.put("recordDate",       updated.getRecordDate());
        if (updated.getPopulation()      != null) updates.put("population",       updated.getPopulation());

        ref.update(updates).get();

        // Return updated document
        Vaccine v = ref.get().get().toObject(Vaccine.class);
        v.setId(id);
        return v;
    }

    // -------------------------------------------------------
    // DELETE VACCINE
    // -------------------------------------------------------
    public void deleteVaccine(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = firestore.collection(COLLECTION).document(id).get().get();
        if (!doc.exists()) throw new RuntimeException("Vaccine record not found with id: " + id);
        firestore.collection(COLLECTION).document(id).delete().get();
    }

    // -------------------------------------------------------
    // STATISTICS for dashboard cards
    // -------------------------------------------------------
    public Map<String, Object> getStatistics() throws ExecutionException, InterruptedException {
        List<Vaccine> all = getAllVaccines();

        long totalVaccinated  = all.stream().mapToLong(v -> v.getTotalVaccinated()  != null ? v.getTotalVaccinated()  : 0).sum();
        long totalDoses       = all.stream().mapToLong(v -> v.getTotalDoses()       != null ? v.getTotalDoses()       : 0).sum();
        long fullyVaccinated  = all.stream().mapToLong(v -> v.getFullyVaccinated()  != null ? v.getFullyVaccinated()  : 0).sum();
        long countries        = all.stream().map(Vaccine::getCountry).filter(Objects::nonNull).distinct().count();
        long vaccineTypes     = all.stream().map(Vaccine::getVaccineName).filter(Objects::nonNull).distinct().count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords",     all.size());
        stats.put("totalVaccinated",  totalVaccinated);
        stats.put("totalDoses",       totalDoses);
        stats.put("fullyVaccinated",  fullyVaccinated);
        stats.put("countriesTracked", countries);
        stats.put("vaccineTypes",     vaccineTypes);
        return stats;
    }

    // -------------------------------------------------------
    // CHART DATA — by country
    // -------------------------------------------------------
    public List<Map<String, Object>> getVaccinationsByCountry()
            throws ExecutionException, InterruptedException {
        List<Vaccine> all = getAllVaccines();

        Map<String, Long> map = new LinkedHashMap<>();
        for (Vaccine v : all) {
            if (v.getCountry() == null) continue;
            map.merge(v.getCountry(), v.getTotalVaccinated() != null ? v.getTotalVaccinated() : 0, Long::sum);
        }

        return map.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("country", e.getKey()); m.put("total", e.getValue()); return m; })
            .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // CHART DATA — by vaccine type
    // -------------------------------------------------------
    public List<Map<String, Object>> getDosesByVaccineType()
            throws ExecutionException, InterruptedException {
        List<Vaccine> all = getAllVaccines();

        Map<String, Long> map = new LinkedHashMap<>();
        for (Vaccine v : all) {
            if (v.getVaccineName() == null) continue;
            map.merge(v.getVaccineName(), v.getTotalDoses() != null ? v.getTotalDoses() : 0, Long::sum);
        }

        return map.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("vaccineName", e.getKey()); m.put("doses", e.getValue()); return m; })
            .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // CHART DATA — trend over time
    // -------------------------------------------------------
    public List<Map<String, Object>> getDosesOverTime(String country)
            throws ExecutionException, InterruptedException {
        List<Vaccine> all = getAllVaccines();

        if (country != null && !country.isBlank() && !country.equalsIgnoreCase("all")) {
            all = all.stream()
                .filter(v -> country.equalsIgnoreCase(v.getCountry()))
                .collect(Collectors.toList());
        }

        Map<String, Long> map = new TreeMap<>();
        for (Vaccine v : all) {
            if (v.getRecordDate() == null) continue;
            map.merge(v.getRecordDate(), v.getTotalDoses() != null ? v.getTotalDoses() : 0, Long::sum);
        }

        return map.entrySet().stream()
            .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("date", e.getKey()); m.put("doses", e.getValue()); return m; })
            .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // GET ALL COUNTRIES (for dropdown)
    // -------------------------------------------------------
    public List<String> getAllCountries() throws ExecutionException, InterruptedException {
        return getAllVaccines().stream()
            .map(Vaccine::getCountry).filter(Objects::nonNull)
            .distinct().sorted().collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // HELPER: Convert Vaccine object to Firestore map
    // -------------------------------------------------------
    private Map<String, Object> vaccineToMap(Vaccine v) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",               v.getId());
        map.put("country",          v.getCountry());
        map.put("vaccineName",      v.getVaccineName());
        map.put("totalVaccinated",  v.getTotalVaccinated());
        map.put("totalDoses",       v.getTotalDoses());
        map.put("fullyVaccinated",  v.getFullyVaccinated());
        map.put("recordDate",       v.getRecordDate());
        map.put("population",       v.getPopulation());
        return map;
    }
}
