package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.CurrentUser
import com.posadeus.fantatennis.infrastructure.security.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CurrentUserConfiguration {

  @Bean
  fun anonymizer(@Value("\${app.security.owner-id-pepper}") pepper: String): Anonymizer =
      Sha256Anonymizer(pepper)

  @Bean
  fun currentUser(anonymizer: Anonymizer): CurrentUser =
      SecurityContextCurrentUser(anonymizer)
}
