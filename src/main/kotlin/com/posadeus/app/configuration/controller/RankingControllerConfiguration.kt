package com.posadeus.app.configuration.controller

import com.posadeus.controller.RankingApi
import com.posadeus.controller.ranking.RankingController
import com.posadeus.domain.service.ranking.RankingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RankingControllerConfiguration {

  @Bean
  open fun rankingApi(rankingService: RankingService): RankingApi =
      RankingController(rankingService)
}