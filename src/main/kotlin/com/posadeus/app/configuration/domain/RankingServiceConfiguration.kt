package com.posadeus.app.configuration.domain

import com.posadeus.domain.infrastructure.AtpTourRepository
import com.posadeus.domain.service.ranking.RankingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RankingServiceConfiguration {

  @Bean
  open fun rankingService(atpTourRepository: AtpTourRepository): RankingService =
      RankingService(atpTourRepository)
}