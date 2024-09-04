package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.job.JobController
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointPersistenceService
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class JobControllerTest {

  private val calculatorService: PlayerFantaPointCalculatorService = mockk()
  private val persistenceService: PlayerFantaPointPersistenceService = mockk()

  private val controller: JobApi = JobController(calculatorService,
                                                 persistenceService)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Test
  fun `204 response`() {

    val domainPlayers = setOf(DomainPlayer(id = "PlayerId1",
                                           tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                              DomainPlayer(id = "PlayerId2",
                                           tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

    every { calculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns domainPlayers
    every { persistenceService.persistScores(domainPlayers) } just runs

    mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
        .andDo(print())
        .andExpect(status().isNoContent)
  }

  companion object {

    private const val UPDATE_PLAYER_FANTA_POINTS_ENDPOINT = "/job/points/"
    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2000
  }
}