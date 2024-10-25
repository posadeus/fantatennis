package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.RankingApi
import com.posadeus.fantatennis.controller.ranking.RankingController
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RankingControllerConfiguration {

  @Bean
  fun rankingApi(rankingService: RankingService): RankingApi =
      RankingController(rankingService)
}