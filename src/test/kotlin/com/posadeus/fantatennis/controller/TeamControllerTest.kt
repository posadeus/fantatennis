package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.controller.team.TeamController
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.team.TeamService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class TeamControllerTest {

  private val service: TeamService = mockk()

  private val controller: TeamApi = TeamController(service)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Test
  fun `200 response - valid team`() {

    val expected = TeamDto(A_LIST_OF_PLAYERS)
    val team = FoundTeam(expected)

    every { service.getTeam(A_USER_ID, A_TEAM_ID) } returns team

    mvc.perform(get("/$A_USER_ID$TEAM_ENDPOINT$A_TEAM_ID")
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk)
        .andExpect(content().json(toJson(expected)))
  }

  @Test
  fun `200 response - empty team`() {

    val team = EmptyTeam

    every { service.getTeam(A_USER_ID, A_TEAM_ID) } returns team

    mvc.perform(get("/$A_USER_ID$TEAM_ENDPOINT$A_TEAM_ID")
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent)
  }

  @Test
  fun `400 response - UserId NOT FOUND`() {

    val team = UserIdNotFoundTeam

    every { service.getTeam(A_USER_ID, A_TEAM_ID) } returns team

    mvc.perform(get("/$A_USER_ID$TEAM_ENDPOINT$A_TEAM_ID")
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest)
  }

  @Test
  fun `400 response - TeamId NOT FOUND`() {

    val team = TeamIdNotFoundTeam

    every { service.getTeam(A_USER_ID, A_TEAM_ID) } returns team

    mvc.perform(get("/$A_USER_ID$TEAM_ENDPOINT$A_TEAM_ID")
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest)
  }

  @Test
  fun `500 response`() {

    val team = ErrorTeam

    every { service.getTeam(A_USER_ID, A_TEAM_ID) } returns team

    mvc.perform(get("/$A_USER_ID$TEAM_ENDPOINT$A_TEAM_ID")
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isInternalServerError)
  }

  @Throws(JsonProcessingException::class)
  private fun toJson(obj: Any): String =
      objectMapper.writeValueAsString(obj)

  companion object {

    private const val TEAM_ENDPOINT = "/team/"
    private const val A_TEAM_ID = "A_TEAM_ID"
    private const val A_USER_ID = "A_USER_ID"

    private val A_LIST_OF_PLAYERS = emptyList<TeamPlayerDto>()
  }
}