package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.controller.job.JobController
import com.posadeus.fantatennis.domain.service.player.FantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.FantaPointPersistenceService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JobControllerConfiguration {

  @Bean
  open fun jobApi(fantaPointCalculatorService: FantaPointCalculatorService,
                  fantaPointPersistenceService: FantaPointPersistenceService): JobApi =
      JobController(fantaPointCalculatorService,
                    fantaPointPersistenceService)
}