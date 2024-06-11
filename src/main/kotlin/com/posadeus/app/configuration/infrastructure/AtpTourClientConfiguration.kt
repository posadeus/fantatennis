package com.posadeus.app.configuration.infrastructure

import com.posadeus.infrastructure.client.atptour.AtpTourClient
import com.posadeus.infrastructure.client.atptour.impl.RestAtpTourClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
open class AtpTourClientConfiguration {

  @Bean
  open fun atpTourGetClient(@Value("\${client.atp-tour.uri}") atpTourURI: String,
                            atpTourRestTemplate: RestTemplate): AtpTourClient =
    RestAtpTourClient(atpTourURI, atpTourRestTemplate)

  @Bean
  open fun atpTourRestTemplate(
    @Value("\${client.atp-tour.http.connection-request-timeout}") connectionRequestTimeout: Int,
    @Value("\${client.atp-tour.http.connection-timeout}") connectionTimeout: Int): RestTemplate {

    val requestFactory = HttpComponentsClientHttpRequestFactory()
    requestFactory.setConnectTimeout(connectionTimeout)
    requestFactory.setConnectionRequestTimeout(connectionRequestTimeout)

    val restTemplate = RestTemplate()
    restTemplate.requestFactory = requestFactory

    return restTemplate
  }
}