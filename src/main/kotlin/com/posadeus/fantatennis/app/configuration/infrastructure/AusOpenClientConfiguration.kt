package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.infrastructure.client.ausopen.AusOpenClient
import com.posadeus.fantatennis.infrastructure.client.ausopen.impl.RestAusOpenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
open class AusOpenClientConfiguration {

  @Bean
  open fun ausOpenClient(@Value("\${client.aus-open.uri}") ausOpenURI: String,
                         ausOpenRestTemplate: RestTemplate): AusOpenClient =
      RestAusOpenClient(ausOpenURI, ausOpenRestTemplate)

  @Bean
  open fun ausOpenRestTemplate(
      @Value("\${client.aus-open.http.connection-request-timeout}") connectionRequestTimeout: Int,
      @Value("\${client.aus-open.http.connection-timeout}") connectionTimeout: Int): RestTemplate {

    val requestFactory = HttpComponentsClientHttpRequestFactory()
    requestFactory.setConnectTimeout(connectionTimeout)
    requestFactory.setConnectionRequestTimeout(connectionRequestTimeout)

    val restTemplate = RestTemplate()
    restTemplate.requestFactory = requestFactory

    return restTemplate
  }
}