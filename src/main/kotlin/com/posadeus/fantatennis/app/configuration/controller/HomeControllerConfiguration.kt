package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.login.HomeController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HomeControllerConfiguration {

  @Bean
  fun homeController(): HomeController =
      HomeController()
}