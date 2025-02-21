package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.job.JobController
import com.posadeus.fantatennis.domain.exception.InvalidYearException
import com.posadeus.fantatennis.domain.exception.NoPointsForTournamentException
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import com.posadeus.fantatennis.domain.service.tournament.AddTournamentsService
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class JobControllerTest {

  private val fantaPointService: FantaPointService = mockk()
  private val addTournamentsService: AddTournamentsService = mockk()

  private val controller: JobApi = JobController(fantaPointService, addTournamentsService)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Nested
  inner class AddTournaments {

    @Test
    fun `204 response`() {

      every { addTournamentsService.addTournamentsFor(A_YEAR) } just runs

      mvc.perform(post("$ADD_TOURNAMENTS_ENDPOINT$A_YEAR"))
          .andDo(print())
          .andExpect(status().isNoContent)
    }

    @Test
    fun `400 response`() {

      every { addTournamentsService.addTournamentsFor(A_YEAR) } throws InvalidYearException(A_YEAR.toString())

      mvc.perform(post("$ADD_TOURNAMENTS_ENDPOINT$A_YEAR"))
          .andDo(print())
          .andExpect(status().isBadRequest)
    }

    @Test
    fun `500 response`() {

      every { addTournamentsService.addTournamentsFor(A_YEAR) } throws Exception()

      mvc.perform(post("$ADD_TOURNAMENTS_ENDPOINT$A_YEAR"))
          .andDo(print())
          .andExpect(status().isInternalServerError)
    }
  }

  @Nested
  inner class UpdatePlayersFantaPoints {

    @Test
    fun `204 response`() {

      every { fantaPointService.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } just runs

      mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
          .andDo(print())
          .andExpect(status().isNoContent)
    }

    @Test
    fun `400 response`() {

      every { fantaPointService.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } throws NoPointsForTournamentException()

      mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
          .andDo(print())
          .andExpect(status().isBadRequest)
    }

    @Test
    fun `500 response`() {

      every { fantaPointService.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } throws Exception()

      mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
          .andDo(print())
          .andExpect(status().isInternalServerError)
    }
  }

  companion object {

    private const val ADD_TOURNAMENTS_ENDPOINT = "/job/tournaments/"
    private const val UPDATE_PLAYER_FANTA_POINTS_ENDPOINT = "/job/points/"
    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2000
  }
}