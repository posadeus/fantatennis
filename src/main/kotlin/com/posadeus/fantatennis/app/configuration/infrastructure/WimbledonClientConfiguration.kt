package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.infrastructure.client.wimbledon.WimbledonClient
import com.posadeus.fantatennis.infrastructure.client.wimbledon.impl.RestWimbledonClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class WimbledonClientConfiguration {

  @Bean
  fun wimbledonClient(@Value("\${client.wimbledon.uri}") wimbledonURI: String,
                           wimbledonRestTemplate: RestTemplate): WimbledonClient =
    RestWimbledonClient(wimbledonURI, wimbledonRestTemplate)

  @Bean
  fun wimbledonRestTemplate(
    @Value("\${client.wimbledon.http.connection-request-timeout}") connectionRequestTimeout: Int,
    @Value("\${client.wimbledon.http.connection-timeout}") connectionTimeout: Int): RestTemplate {

    val requestFactory = HttpComponentsClientHttpRequestFactory()
    requestFactory.setConnectTimeout(connectionTimeout)
    requestFactory.setConnectionRequestTimeout(connectionRequestTimeout)

    val restTemplate = RestTemplate()
    restTemplate.requestFactory = requestFactory

    return restTemplate
  }
}