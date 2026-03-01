package com.example.demo.config;

// ============================================================
// DataSeeder.java — AUTO-POPULATE SAMPLE DATA INTO FIRESTORE
// Runs on startup. If "vaccines" collection is empty, inserts 30 records.
// ============================================================

import com.example.demo.model.Vaccine;
import com.example.demo.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private VaccineService vaccineService;

    @Override
    public void run(String... args) throws Exception {
        List<Vaccine> existing = vaccineService.getAllVaccines();
        if (existing.isEmpty()) {
            System.out.println("Seeding Firestore with sample vaccine data...");
            seedData();
            System.out.println("Done! Firestore seeded successfully.");
        } else {
            System.out.println("Firestore already has data — skipping seed.");
        }
    }

    private void seedData() throws Exception {
        List<Vaccine> records = Arrays.asList(
            new Vaccine("India",        "Covaxin",             850_000_000L, 1_400_000_000L, 700_000_000L, "2023-01-15", 1_400_000_000L),
            new Vaccine("India",        "Covishield",          950_000_000L, 1_800_000_000L, 880_000_000L, "2023-03-20", 1_400_000_000L),
            new Vaccine("India",        "Covaxin",             870_000_000L, 1_450_000_000L, 720_000_000L, "2023-06-10", 1_400_000_000L),
            new Vaccine("USA",          "Pfizer-BioNTech",     200_000_000L,   380_000_000L, 160_000_000L, "2023-01-10",   331_000_000L),
            new Vaccine("USA",          "Moderna",             140_000_000L,   260_000_000L, 110_000_000L, "2023-02-14",   331_000_000L),
            new Vaccine("USA",          "Johnson & Johnson",    18_000_000L,    18_000_000L,  18_000_000L, "2023-03-05",   331_000_000L),
            new Vaccine("USA",          "Pfizer-BioNTech",     210_000_000L,   400_000_000L, 170_000_000L, "2023-06-15",   331_000_000L),
            new Vaccine("Brazil",       "Oxford-AstraZeneca",  120_000_000L,   200_000_000L,  95_000_000L, "2023-01-20",   213_000_000L),
            new Vaccine("Brazil",       "Pfizer-BioNTech",      80_000_000L,   140_000_000L,  65_000_000L, "2023-04-08",   213_000_000L),
            new Vaccine("Brazil",       "CoronaVac",            60_000_000L,   100_000_000L,  50_000_000L, "2023-06-22",   213_000_000L),
            new Vaccine("UK",           "Oxford-AstraZeneca",   45_000_000L,    80_000_000L,  38_000_000L, "2023-01-05",    67_000_000L),
            new Vaccine("UK",           "Pfizer-BioNTech",      35_000_000L,    65_000_000L,  30_000_000L, "2023-03-18",    67_000_000L),
            new Vaccine("UK",           "Moderna",              10_000_000L,    18_000_000L,   8_500_000L, "2023-06-05",    67_000_000L),
            new Vaccine("Germany",      "Pfizer-BioNTech",      42_000_000L,    78_000_000L,  36_000_000L, "2023-02-12",    83_000_000L),
            new Vaccine("Germany",      "Moderna",              15_000_000L,    28_000_000L,  13_000_000L, "2023-04-25",    83_000_000L),
            new Vaccine("France",       "Pfizer-BioNTech",      38_000_000L,    72_000_000L,  33_000_000L, "2023-02-08",    67_000_000L),
            new Vaccine("France",       "Moderna",              12_000_000L,    22_000_000L,  10_500_000L, "2023-05-14",    67_000_000L),
            new Vaccine("China",        "Sinovac",             900_000_000L, 1_600_000_000L, 820_000_000L, "2023-01-25", 1_400_000_000L),
            new Vaccine("China",        "Sinopharm",           800_000_000L, 1_400_000_000L, 740_000_000L, "2023-04-30", 1_400_000_000L),
            new Vaccine("Japan",        "Pfizer-BioNTech",      80_000_000L,   180_000_000L,  70_000_000L, "2023-03-10",   125_000_000L),
            new Vaccine("Japan",        "Moderna",              20_000_000L,    40_000_000L,  18_000_000L, "2023-05-20",   125_000_000L),
            new Vaccine("Canada",       "Pfizer-BioNTech",      25_000_000L,    48_000_000L,  22_000_000L, "2023-02-22",    38_000_000L),
            new Vaccine("Canada",       "Moderna",               9_000_000L,    17_000_000L,   8_000_000L, "2023-04-15",    38_000_000L),
            new Vaccine("Australia",    "Pfizer-BioNTech",      14_000_000L,    26_000_000L,  12_500_000L, "2023-03-28",    26_000_000L),
            new Vaccine("Australia",    "Oxford-AstraZeneca",    8_000_000L,    14_500_000L,   7_000_000L, "2023-06-01",    26_000_000L),
            new Vaccine("South Africa", "Johnson & Johnson",    15_000_000L,    15_000_000L,  15_000_000L, "2023-01-30",    60_000_000L),
            new Vaccine("South Africa", "Pfizer-BioNTech",       8_000_000L,    14_000_000L,   7_000_000L, "2023-05-08",    60_000_000L),
            new Vaccine("Indonesia",    "Sinovac",              95_000_000L,   160_000_000L,  80_000_000L, "2023-02-15",   273_000_000L),
            new Vaccine("Mexico",       "Oxford-AstraZeneca",   22_000_000L,    40_000_000L,  18_000_000L, "2023-03-05",   128_000_000L),
            new Vaccine("Mexico",       "Pfizer-BioNTech",      18_000_000L,    34_000_000L,  15_000_000L, "2023-06-10",   128_000_000L)
        );

        for (Vaccine v : records) {
            vaccineService.addVaccine(v);
        }
    }
}
