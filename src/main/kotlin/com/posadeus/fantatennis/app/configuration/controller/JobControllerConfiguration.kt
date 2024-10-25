package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.controller.job.JobController
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JobControllerConfiguration {

  @Bean
  fun jobApi(fantaPointService: FantaPointService): JobApi =
      JobController(fantaPointService)
}