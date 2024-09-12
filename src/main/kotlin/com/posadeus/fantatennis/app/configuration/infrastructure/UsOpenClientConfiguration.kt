package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.infrastructure.client.usopen.UsOpenClient
import com.posadeus.fantatennis.infrastructure.client.usopen.impl.RestUsOpenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
open class UsOpenClientConfiguration {

  @Bean
  open fun usOpenClient(@Value("\${client.us-open.uri}") usOpenURI: String,
                        usOpenRestTemplate: RestTemplate): UsOpenClient =
      RestUsOpenClient(usOpenURI, usOpenRestTemplate)

  @Bean
  open fun usOpenRestTemplate(
      @Value("\${client.us-open.http.connection-request-timeout}") connectionRequestTimeout: Int,
      @Value("\${client.us-open.http.connection-timeout}") connectionTimeout: Int): RestTemplate {

    val requestFactory = HttpComponentsClientHttpRequestFactory()
    requestFactory.setConnectTimeout(connectionTimeout)
    requestFactory.setConnectionRequestTimeout(connectionRequestTimeout)

    val restTemplate = RestTemplate()
    restTemplate.requestFactory = requestFactory

    return restTemplate
  }
}