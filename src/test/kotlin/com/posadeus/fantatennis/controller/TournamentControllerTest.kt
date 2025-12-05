package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.model.TestTournamentDto.aTournamentDto
import com.posadeus.fantatennis.controller.model.fantatournament.*
import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.controller.tournament.TournamentController
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.RetrieveTournamentService
import com.posadeus.fantatennis.domain.service.RetrieveTournamentsService
import com.posadeus.fantatennis.domain.service.fantatournament.*
import com.posadeus.fantatennis.domain.service.tournament.*
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class TournamentControllerTest {

  private val retrieveTournamentService: RetrieveTournamentService = mockk()
  private val retrieveTournamentsService: RetrieveTournamentsService = mockk()

  private val controller: TournamentApi = TournamentController(retrieveTournamentService,
                                                               retrieveTournamentsService)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Nested
  inner class RetrieveTournament {

    @Test
    fun `200 response`() {

      val expected = TournamentDto(tournamentName = A_TOURNAMENT_NAME,
                                   tournamentPoints = A_TOURNAMENT_POINTS,
                                   playersScore = listOf(PlayerPointsDto(fullName = A_PLAYER_FULL_NAME,
                                                                         fantaPoints = A_FANTA_POINTS),
                                                         PlayerPointsDto(fullName = ANOTHER_PLAYER_FULL_NAME,
                                                                         fantaPoints = ANOTHER_FANTA_POINTS)))
      val tournament = FoundTournamentResults(expected)

      every { retrieveTournamentService.retrieve(A_TOURNAMENT_ID) } returns tournament

      mvc.perform(get("$TOURNAMENT_ENDPOINT/$A_TOURNAMENT_ID")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk)
          .andExpect(content().json(toJson(expected)))
    }

    @Test
    fun `400 response - not found tournament id`() {

      val tournament = NotFoundTournamentId

      every { retrieveTournamentService.retrieve(A_TOURNAMENT_ID) } returns tournament

      mvc.perform(get("$TOURNAMENT_ENDPOINT/$A_TOURNAMENT_ID")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest)
    }

    @Test
    fun `500 response`() {

      val tournament = ErrorTournamentResults

      every { retrieveTournamentService.retrieve(A_TOURNAMENT_ID) } returns tournament

      mvc.perform(get("$TOURNAMENT_ENDPOINT/$A_TOURNAMENT_ID")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isInternalServerError)
    }
  }

  @Nested
  inner class RetrieveAllTournaments {

    @Test
    fun `200 response`() {

      val expected = TournamentsDto(tournaments = listOf(aTournamentDto(), aTournamentDto()))
      val tournaments = FoundTournamentsResults(expected)

      every { retrieveTournamentsService.retrieveAllBy(A_YEAR) } returns tournaments

      mvc.perform(get("$TOURNAMENTS_ENDPOINT/$A_YEAR")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk)
          .andExpect(content().json(toJson(expected)))
    }

    @Test
    fun `404 response - not found tournaments`() {

      val tournaments = NotFoundTournaments

      every { retrieveTournamentsService.retrieveAllBy(A_YEAR) } returns tournaments

      mvc.perform(get("$TOURNAMENTS_ENDPOINT/$A_YEAR")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isNotFound)
    }

    @Test
    fun `500 response`() {

      val tournaments = ErrorTournamentsResults

      every { retrieveTournamentsService.retrieveAllBy(A_YEAR) } returns tournaments

      mvc.perform(get("$TOURNAMENTS_ENDPOINT/$A_YEAR")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isInternalServerError)
    }
  }

  @Throws(JsonProcessingException::class)
  private fun toJson(obj: Any): String =
      objectMapper.writeValueAsString(obj)

  companion object {

    private const val TOURNAMENT_ENDPOINT = "/tournament"
    private const val TOURNAMENTS_ENDPOINT = "/tournaments"
    private const val A_TOURNAMENT_NAME = "A_TOURNAMENT_NAME"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val ANOTHER_PLAYER_FULL_NAME = "ANOTHER_PLAYER_FULL_NAME"
    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2000
    private const val A_TOURNAMENT_POINTS = 250
    private const val A_FANTA_POINTS = 10.0
    private const val ANOTHER_FANTA_POINTS = 8.0
  }
}