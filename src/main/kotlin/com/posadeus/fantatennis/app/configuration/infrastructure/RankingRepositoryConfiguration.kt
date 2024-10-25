package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.RankingRepository
import com.posadeus.fantatennis.infrastructure.client.atptour.AtpTourClient
import com.posadeus.fantatennis.infrastructure.repository.atptour.AtpTourRankingRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RankingRepositoryConfiguration {

  @Bean
  fun rankingRepository(atpTourClient: AtpTourClient): RankingRepository =
      AtpTourRankingRepository(atpTourClient)
}