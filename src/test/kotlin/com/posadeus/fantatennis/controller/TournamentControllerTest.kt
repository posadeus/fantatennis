package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.controller.model.tournament.*
import com.posadeus.fantatennis.controller.tournament.TournamentController
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.tournament.CreateTournamentService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentService
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

  private val createTournamentService: CreateTournamentService = mockk()
  private val retrieveTournamentService: RetrieveTournamentService = mockk()

  private val controller: TournamentApi = TournamentController(createTournamentService,
                                                               retrieveTournamentService)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Nested
  inner class CreateTournament {

    @Test
    fun `200 response`() {

      val request = TournamentToCreateDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                          tournamentYear = A_TOURNAMENT_YEAR)


      val expected = TournamentCreatedDto(id = A_TOURNAMENT_ID,
                                          startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                          tournamentYear = A_TOURNAMENT_YEAR)
      val tournamentCreated = SuccessTournamentCreated(expected)

      every { createTournamentService.create(request) } returns tournamentCreated

      mvc.perform(post(TOURNAMENT_ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isOk)
          .andExpect(content().json(toJson(expected)))
    }

    @Test
    fun `400 response - incorrect request body`() {

      val request = """
        {
          "invalid": "request"
        }
      """.trimIndent()

      mvc.perform(post(TOURNAMENT_ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isBadRequest)

      verify { createTournamentService wasNot called }
    }

    @Test
    fun `500 response`() {

      val request = TournamentToCreateDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                          tournamentYear = A_TOURNAMENT_YEAR)

      every { createTournamentService.create(request) } returns ErrorTournamentCreation

      mvc.perform(post(TOURNAMENT_ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isInternalServerError)
    }
  }

  @Nested
  inner class RetrieveTournament {

    @Test
    fun `200 response`() {

      val expected = TournamentDto(teams = A_TEAM_LIST)
      val tournament = FoundFantaTournamentResults(expected)

      every { retrieveTournamentService.retrieve(A_TOURNAMENT_ID) } returns tournament

      mvc.perform(get("$TOURNAMENT_ENDPOINT/$A_TOURNAMENT_ID")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk)
          .andExpect(content().json(toJson(expected)))
    }

    @Test
    fun `400 response - not found tournament id`() {

      val tournament = NotFoundFantaTournamentId

      every { retrieveTournamentService.retrieve(A_TOURNAMENT_ID) } returns tournament

      mvc.perform(get("$TOURNAMENT_ENDPOINT/$A_TOURNAMENT_ID")
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest)
    }

    @Test
    fun `500 response`() {

      val tournament = ErrorFantaTournamentResults

      every { retrieveTournamentService.retrieve(A_TOURNAMENT_ID) } returns tournament

      mvc.perform(get("$TOURNAMENT_ENDPOINT/$A_TOURNAMENT_ID")
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
    private const val A_TOURNAMENT_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 1
    private const val A_TOURNAMENT_YEAR = 2024

    private val A_TEAM_LIST = listOf<TeamDto>()
  }
}