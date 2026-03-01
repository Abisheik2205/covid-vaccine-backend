package com.example.demo.config;

// ============================================================
// FirebaseConfig.java — SECURE VERSION
// Reads Firebase credentials from ENVIRONMENT VARIABLE
// Never reads from a file directly — safe for GitHub & Cloud Run
// ============================================================

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore() throws IOException {

        if (FirebaseApp.getApps().isEmpty()) {

            // Read the entire firebase-key.json content from environment variable
            // Set this in: Cloud Run → Environment Variables → FIREBASE_CREDENTIALS
            String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");

            if (firebaseCredentials == null || firebaseCredentials.isBlank()) {
                throw new IllegalStateException(
                    "❌ FIREBASE_CREDENTIALS environment variable is not set! " +
                    "Please set it in Cloud Run or your local .env"
                );
            }

            // Convert the JSON string into an InputStream for Firebase SDK
            ByteArrayInputStream credentialsStream = new ByteArrayInputStream(
                firebaseCredentials.getBytes(StandardCharsets.UTF_8)
            );

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase connected successfully!");
        }

        return FirestoreClient.getFirestore();
    }
}
