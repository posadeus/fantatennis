package com.posadeus.fantatennis.app.configuration.infrastructure

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.FileInputStream
import java.io.IOException
import java.util.*

@Configuration
open class FirebaseConfiguration {

  @PostConstruct
  fun initialize() {

    try {

      val resource = ClassPathResource("firebase-adminsdk.json")
      val serviceAccount = resource.inputStream
      val properties = Properties()
      val propertiesFile = ClassPathResource("firebase-config.properties").file
            FileInputStream(propertiesFile).use { input ->
                properties.load(input)
            }
      val databaseUrl = properties.getProperty("firebase.database.url")
            ?: throw IllegalStateException("Database URL not found in configuration")

      val options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setDatabaseUrl(databaseUrl)
          .build()

      FirebaseApp.initializeApp(options)
    }
    catch (e: IOException) {

      e.printStackTrace()
    }
  }

  @Bean
  open fun firebaseDatabase(): FirebaseDatabase =
      FirebaseDatabase.getInstance()
}