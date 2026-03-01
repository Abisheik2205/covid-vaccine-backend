package com.example.demo.config;


//============================================================
//WHAT IS THIS FILE?
//DataSeeder automatically inserts sample data into the database
//when the application starts — BUT only if the table is empty.
//
//WHY?
//Your PostgreSQL database on Render starts completely empty.
//Without seed data, your charts and dashboard would show nothing.
//This gives you real-looking data to work with immediately.
//
//HOW IT WORKS:
//Spring Boot runs CommandLineRunner after the app starts.
//We check if the database is empty, and if so, insert records.
//============================================================

import com.example.demo.model.Vaccine;
import com.example.demo.repository.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component   // Spring automatically picks this up and runs it on startup
public class DataSeeder implements CommandLineRunner {

 @Autowired
 private VaccineRepository vaccineRepository;

 @Override
 public void run(String... args) throws Exception {
     // Only seed if the database is empty — won't duplicate data on restarts
     if (vaccineRepository.count() == 0) {
         System.out.println("🌱 Seeding database with sample vaccine data...");
         seedData();
         System.out.println("✅ Database seeded with " + vaccineRepository.count() + " records.");
     } else {
         System.out.println("✅ Database already has data — skipping seed.");
     }
 }

 private void seedData() {
     List<Vaccine> records = Arrays.asList(
         // India
         new Vaccine("India", "Covaxin",         850_000_000L, 1_400_000_000L, 700_000_000L, LocalDate.of(2023, 1, 15), 1_400_000_000L),
         new Vaccine("India", "Covishield",       950_000_000L, 1_800_000_000L, 880_000_000L, LocalDate.of(2023, 3, 20), 1_400_000_000L),
         new Vaccine("India", "Covaxin",          870_000_000L, 1_450_000_000L, 720_000_000L, LocalDate.of(2023, 6, 10), 1_400_000_000L),

         // USA
         new Vaccine("USA", "Pfizer-BioNTech",   200_000_000L,  380_000_000L, 160_000_000L, LocalDate.of(2023, 1, 10), 331_000_000L),
         new Vaccine("USA", "Moderna",            140_000_000L,  260_000_000L, 110_000_000L, LocalDate.of(2023, 2, 14), 331_000_000L),
         new Vaccine("USA", "Johnson & Johnson",   18_000_000L,   18_000_000L,  18_000_000L, LocalDate.of(2023, 3, 5),  331_000_000L),
         new Vaccine("USA", "Pfizer-BioNTech",   210_000_000L,  400_000_000L, 170_000_000L, LocalDate.of(2023, 6, 15), 331_000_000L),

         // Brazil
         new Vaccine("Brazil", "Oxford-AstraZeneca", 120_000_000L, 200_000_000L,  95_000_000L, LocalDate.of(2023, 1, 20), 213_000_000L),
         new Vaccine("Brazil", "Pfizer-BioNTech",     80_000_000L, 140_000_000L,  65_000_000L, LocalDate.of(2023, 4, 8),  213_000_000L),
         new Vaccine("Brazil", "CoronaVac",           60_000_000L, 100_000_000L,  50_000_000L, LocalDate.of(2023, 6, 22), 213_000_000L),

         // UK
         new Vaccine("UK", "Oxford-AstraZeneca",  45_000_000L,  80_000_000L,  38_000_000L, LocalDate.of(2023, 1, 5),  67_000_000L),
         new Vaccine("UK", "Pfizer-BioNTech",     35_000_000L,  65_000_000L,  30_000_000L, LocalDate.of(2023, 3, 18), 67_000_000L),
         new Vaccine("UK", "Moderna",             10_000_000L,  18_000_000L,   8_500_000L, LocalDate.of(2023, 6, 5),  67_000_000L),

         // Germany
         new Vaccine("Germany", "Pfizer-BioNTech", 42_000_000L, 78_000_000L, 36_000_000L, LocalDate.of(2023, 2, 12), 83_000_000L),
         new Vaccine("Germany", "Moderna",         15_000_000L, 28_000_000L, 13_000_000L, LocalDate.of(2023, 4, 25), 83_000_000L),

         // France
         new Vaccine("France", "Pfizer-BioNTech", 38_000_000L, 72_000_000L, 33_000_000L, LocalDate.of(2023, 2, 8),  67_000_000L),
         new Vaccine("France", "Moderna",         12_000_000L, 22_000_000L, 10_500_000L, LocalDate.of(2023, 5, 14), 67_000_000L),

         // China
         new Vaccine("China", "Sinovac",         900_000_000L, 1_600_000_000L, 820_000_000L, LocalDate.of(2023, 1, 25), 1_400_000_000L),
         new Vaccine("China", "Sinopharm",       800_000_000L, 1_400_000_000L, 740_000_000L, LocalDate.of(2023, 4, 30), 1_400_000_000L),

         // Japan
         new Vaccine("Japan", "Pfizer-BioNTech", 80_000_000L, 180_000_000L, 70_000_000L, LocalDate.of(2023, 3, 10), 125_000_000L),
         new Vaccine("Japan", "Moderna",         20_000_000L,  40_000_000L, 18_000_000L, LocalDate.of(2023, 5, 20), 125_000_000L),

         // Canada
         new Vaccine("Canada", "Pfizer-BioNTech", 25_000_000L, 48_000_000L, 22_000_000L, LocalDate.of(2023, 2, 22), 38_000_000L),
         new Vaccine("Canada", "Moderna",          9_000_000L, 17_000_000L,  8_000_000L, LocalDate.of(2023, 4, 15), 38_000_000L),

         // Australia
         new Vaccine("Australia", "Pfizer-BioNTech",  14_000_000L, 26_000_000L, 12_500_000L, LocalDate.of(2023, 3, 28), 26_000_000L),
         new Vaccine("Australia", "Oxford-AstraZeneca", 8_000_000L, 14_500_000L,  7_000_000L, LocalDate.of(2023, 6, 1),  26_000_000L),

         // South Africa
         new Vaccine("South Africa", "Johnson & Johnson", 15_000_000L, 15_000_000L, 15_000_000L, LocalDate.of(2023, 1, 30), 60_000_000L),
         new Vaccine("South Africa", "Pfizer-BioNTech",    8_000_000L, 14_000_000L,  7_000_000L, LocalDate.of(2023, 5, 8),  60_000_000L),

         // Indonesia
         new Vaccine("Indonesia", "Sinovac",       95_000_000L, 160_000_000L, 80_000_000L, LocalDate.of(2023, 2, 15), 273_000_000L),
         new Vaccine("Indonesia", "Oxford-AstraZeneca", 30_000_000L, 55_000_000L, 26_000_000L, LocalDate.of(2023, 5, 22), 273_000_000L),

         // Mexico
         new Vaccine("Mexico", "Oxford-AstraZeneca", 22_000_000L, 40_000_000L, 18_000_000L, LocalDate.of(2023, 3, 5),  128_000_000L),
         new Vaccine("Mexico", "Pfizer-BioNTech",    18_000_000L, 34_000_000L, 15_000_000L, LocalDate.of(2023, 6, 10), 128_000_000L)
     );

     vaccineRepository.saveAll(records);
 }
}

