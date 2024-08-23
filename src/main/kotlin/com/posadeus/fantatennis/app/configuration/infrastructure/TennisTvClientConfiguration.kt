package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.impl.RestTennisTvClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
open class TennisTvClientConfiguration {

  @Bean
  open fun tennisTvGetClient(@Value("\${client.tennis-tv.uri}") tennisTvURI: String,
                            tennisTvRestTemplate: RestTemplate): TennisTvClient =
    RestTennisTvClient(tennisTvURI, tennisTvRestTemplate)

  @Bean
  open fun tennisTvRestTemplate(
    @Value("\${client.tennis-tv.http.connection-request-timeout}") connectionRequestTimeout: Int,
    @Value("\${client.tennis-tv.http.connection-timeout}") connectionTimeout: Int): RestTemplate {

    val requestFactory = HttpComponentsClientHttpRequestFactory()
    requestFactory.setConnectTimeout(connectionTimeout)
    requestFactory.setConnectionRequestTimeout(connectionRequestTimeout)

    val restTemplate = RestTemplate()
    restTemplate.requestFactory = requestFactory

    return restTemplate
  }
}