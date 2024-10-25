package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.RankingRepository
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RankingServiceConfiguration {

  @Bean
  fun rankingService(rankingRepository: RankingRepository): RankingService =
      RankingService(rankingRepository)
}