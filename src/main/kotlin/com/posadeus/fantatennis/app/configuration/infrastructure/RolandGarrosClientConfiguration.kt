package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.infrastructure.client.rolandgarros.RolandGarrosClient
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.impl.RestRolandGarrosClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RolandGarrosClientConfiguration {

  @Bean
  fun rolandGarrosClient(@Value("\${client.roland-garros.uri}") rolandGarrosURI: String,
                         rolandGarrosRestTemplate: RestTemplate): RolandGarrosClient =
      RestRolandGarrosClient(rolandGarrosURI, rolandGarrosRestTemplate)

  @Bean
  fun rolandGarrosRestTemplate(
      @Value("\${client.roland-garros.http.connection-request-timeout}") connectionRequestTimeout: Int,
      @Value("\${client.roland-garros.http.connection-timeout}") connectionTimeout: Int): RestTemplate {

    val requestFactory = HttpComponentsClientHttpRequestFactory()
    requestFactory.setConnectTimeout(connectionTimeout)
    requestFactory.setConnectionRequestTimeout(connectionRequestTimeout)

    val restTemplate = RestTemplate()
    restTemplate.requestFactory = requestFactory

    return restTemplate
  }
}