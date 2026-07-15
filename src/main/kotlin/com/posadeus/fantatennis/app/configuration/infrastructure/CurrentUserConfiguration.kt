package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.CurrentUser
import com.posadeus.fantatennis.infrastructure.security.SecurityContextCurrentUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CurrentUserConfiguration {

  @Bean
  fun currentUser(): CurrentUser =
      SecurityContextCurrentUser()
}