package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.controller.team.TeamController
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.CreateTeamService
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

  private val controller: TeamApi = TeamController(service, createTeamService)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Nested
  inner class Create {

    @Test
    fun `201 response`() {

      val request = TeamToCreateDto(ownerId = "OWNER_ID")
      val expected = TeamCreatedDto(id = 1, ownerId = "OWNER_ID")

      every { createTeamService.create(request) } returns expected

      mvc.perform(post(TEAM_ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .accept(MediaType.APPLICATION_JSON)
                      .content(toJson(request)))
          .andDo(print())
          .andExpect(status().isCreated)
          .andExpect(content().json(toJson(expected)))

      verify(exactly = 1) { createTeamService.create(request) }
      verify { service wasNot called }
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
    }

    @Test
    fun `400 response - TeamId NOT FOUND`() {

      val team = TeamIdNotFoundTeam

      every { service.getTeam(A_TEAM_ID) } returns team

      mvc.perform(get("$TEAM_ENDPOINT/$A_TEAM_ID")
                      .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest)
    }

    @Test
    fun `500 response`() {

      val team = ErrorTeam

      every { service.getTeam(A_TEAM_ID) } returns team

      mvc.perform(get("$TEAM_ENDPOINT/$A_TEAM_ID")
                      .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isInternalServerError)
    }
  }

  @Throws(JsonProcessingException::class)
  private fun toJson(obj: Any): String =
      objectMapper.writeValueAsString(obj)

  companion object {

    private const val TEAM_ENDPOINT = "/team"
    private const val A_TEAM_ID = 1
    private const val A_TOTAL_SCORE = 33.3

    private val A_LIST_OF_PLAYERS = emptyList<TeamPlayerDto>()
  }
}