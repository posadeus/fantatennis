package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.controller.team.TeamController
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.TeamService
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class TeamControllerTest {

  private val service: TeamService = mockk()
  private val createTeamService: CreateTeamService = mockk()
  private val addPlayersTeamService: AddPlayersTeamService = mockk()

  private val controller: TeamApi = TeamController(service, createTeamService, addPlayersTeamService)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Nested
  inner class AddPlayers {

    @Test
    fun `200 response - players added`() {

      val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)
      val request = PlayersToAddDto(playerIds = playerIds)

      val expected = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 0.0),
                                              TeamPlayerDto(fullName = ANOTHER_PLAYER_FULL_NAME, fantaPoints = 0.0)),
                             totalScore = 0.0)
      val foundTeam = FoundTeam(expected)

      every { addPlayersTeamService.addPlayers(A_TEAM_ID, playerIds) } returns foundTeam

      mvc.perform(post("$TEAM_ENDPOINT/$A_TEAM_ID/$PLAYER_PATH")
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isOk)
          .andExpect(content().json(toJson(expected)))

      verify(exactly = 1) { addPlayersTeamService.addPlayers(A_TEAM_ID, playerIds) }
      verify { createTeamService wasNot called }
      verify { service wasNot called }
    }

    @Test
    fun `400 response - incorrect request body`() {

      mvc.perform(post("$TEAM_ENDPOINT/$A_TEAM_ID/$PLAYER_PATH")
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest)

      verify { addPlayersTeamService wasNot called }
      verify { createTeamService wasNot called }
      verify { service wasNot called }
    }

    @Test
    fun `400 response - team id not found`() {

      val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)
      val request = PlayersToAddDto(playerIds = playerIds)

      every { addPlayersTeamService.addPlayers(A_TEAM_ID, playerIds) } returns TeamIdNotFoundTeam

      mvc.perform(post("$TEAM_ENDPOINT/$A_TEAM_ID/$PLAYER_PATH")
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isBadRequest)

      verify(exactly = 1) { addPlayersTeamService.addPlayers(A_TEAM_ID, playerIds) }
      verify { createTeamService wasNot called }
      verify { service wasNot called }
    }

    @Test
    fun `500 response`() {

      val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)
      val request = PlayersToAddDto(playerIds = playerIds)

      every { addPlayersTeamService.addPlayers(A_TEAM_ID, playerIds) } returns ErrorTeam

      mvc.perform(post("$TEAM_ENDPOINT/$A_TEAM_ID/$PLAYER_PATH")
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isInternalServerError)

      verify(exactly = 1) { addPlayersTeamService.addPlayers(A_TEAM_ID, playerIds) }
      verify { createTeamService wasNot called }
      verify { service wasNot called }
    }
  }

  @Nested
  inner class Create {

    @Test
    fun `201 response`() {

      val request = TeamToCreateDto(ownerId = "OWNER_ID")
      val expected = TeamCreatedDto(id = 1, ownerId = "OWNER_ID")

      every { createTeamService.create(request) } returns TeamCreated(team = expected)

      mvc.perform(post(TEAM_ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isCreated)
          .andExpect(content().json(toJson(expected)))

      verify(exactly = 1) { createTeamService.create(request) }
      verify { service wasNot called }
      verify { addPlayersTeamService wasNot called }
    }

    @Test
    fun `400 response`() {

      mvc.perform(post(TEAM_ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest)

      verify { createTeamService wasNot called }
      verify { service wasNot called }
      verify { addPlayersTeamService wasNot called }
    }

    @Test
    fun `500 response`() {

      val request = TeamToCreateDto(ownerId = "OWNER_ID")

      every { createTeamService.create(request) } returns ErrorTeamCreation

      mvc.perform(post(TEAM_ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isInternalServerError)

      verify(exactly = 1) { createTeamService.create(request) }
      verify { service wasNot called }
      verify { addPlayersTeamService wasNot called }
    }
  }

  @Nested
  inner class Retrieve {

    @Test
    fun `200 response - valid team`() {

      val expected = TeamDto(A_LIST_OF_PLAYERS, A_TOTAL_SCORE)
      val team = FoundTeam(expected)

      every { service.getTeam(A_TEAM_ID) } returns team

      mvc.perform(get("$TEAM_ENDPOINT/$A_TEAM_ID")
                      .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk)
          .andExpect(content().json(toJson(expected)))

      verify { createTeamService wasNot called }
      verify { addPlayersTeamService wasNot called }
    }

    @Test
    fun `400 response - TeamId NOT FOUND`() {

      val team = TeamIdNotFoundTeam

      every { service.getTeam(A_TEAM_ID) } returns team

      mvc.perform(get("$TEAM_ENDPOINT/$A_TEAM_ID")
                      .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest)

      verify { createTeamService wasNot called }
      verify { addPlayersTeamService wasNot called }
    }

    @Test
    fun `500 response`() {

      val team = ErrorTeam

      every { service.getTeam(A_TEAM_ID) } returns team

      mvc.perform(get("$TEAM_ENDPOINT/$A_TEAM_ID")
                      .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isInternalServerError)

      verify { createTeamService wasNot called }
      verify { addPlayersTeamService wasNot called }
    }
  }

  @Throws(JsonProcessingException::class)
  private fun toJson(obj: Any): String =
      objectMapper.writeValueAsString(obj)

  companion object {

    private const val TEAM_ENDPOINT = "/team"
    private const val PLAYER_PATH = "players"
    private const val A_TEAM_ID = 1
    private const val A_TOTAL_SCORE = 33.3
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val ANOTHER_PLAYER_FULL_NAME = "ANOTHER_PLAYER_FULL_NAME"

    private val A_LIST_OF_PLAYERS = emptyList<TeamPlayerDto>()
  }
}