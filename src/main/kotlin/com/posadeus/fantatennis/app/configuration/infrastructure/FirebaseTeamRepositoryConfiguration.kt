package com.posadeus.fantatennis.app.configuration.infrastructure

import com.google.firebase.database.FirebaseDatabase
import com.posadeus.fantatennis.infrastructure.client.firebase.team.FirebasePlayerDao
import com.posadeus.fantatennis.infrastructure.client.firebase.team.FirebaseTeamDao
import com.posadeus.fantatennis.infrastructure.repository.firebase.FirebaseTeamRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FirebaseTeamRepositoryConfiguration {

  @Bean
  open fun firebaseTeamRepository(firebaseTeamDao: FirebaseTeamDao,
                                  firebasePlayerDao: FirebasePlayerDao): FirebaseTeamRepository =
      FirebaseTeamRepository(firebaseTeamDao,
                             firebasePlayerDao)

  @Bean
  open fun firebaseTeamDao(firebaseDatabase: FirebaseDatabase): FirebaseTeamDao =
      FirebaseTeamDao(firebaseDatabase.getReference("teams"))

  @Bean
  open fun firebasePlayerDao(firebaseDatabase: FirebaseDatabase): FirebasePlayerDao =
      FirebasePlayerDao(firebaseDatabase.getReference("players"))
}