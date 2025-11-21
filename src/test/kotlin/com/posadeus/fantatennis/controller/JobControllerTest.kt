package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.job.JobController
import com.posadeus.fantatennis.domain.exception.InvalidYearException
import com.posadeus.fantatennis.domain.model.FailureReason.*
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.*
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import com.posadeus.fantatennis.domain.service.tournament.AddTournamentsService
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.openapitools.model.JobSucceedWithErrorsDto
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
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

      every { fantaPointService.updateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns FantaPointPersistenceSuccess

      mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
          .andDo(print())
          .andExpect(status().isNoContent)
    }

    @Test
    fun `200 response`() {

      val expected = JobSucceedWithErrorsDto("Job succeed with following error: I failed")

      every { fantaPointService.updateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns FantaPointPersistenceSucceedWithErrors("I failed")

      mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
          .andDo(print())
          .andExpect(status().isOk)
        .andExpect(content().json(toJson(expected)))
    }

    @Test
    fun `400 response`() {

      every {
        fantaPointService.updateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR)
      } returns FantaPointPersistenceFailure(NO_POINTS_FOR_TOURNAMENT)

      mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
          .andDo(print())
          .andExpect(status().isBadRequest)
    }

    @Test
    fun `500 response`() {

      every { fantaPointService.updateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns FantaPointPersistenceFailure(PERSISTENCE_ERROR)

      mvc.perform(post("$UPDATE_PLAYER_FANTA_POINTS_ENDPOINT$A_TOURNAMENT_ID/$A_YEAR"))
          .andDo(print())
          .andExpect(status().isInternalServerError)
    }
  }

  @Throws(JsonProcessingException::class)
  private fun toJson(obj: Any): String =
      objectMapper.writeValueAsString(obj)

  companion object {

    private const val ADD_TOURNAMENTS_ENDPOINT = "/job/tournaments/"
    private const val UPDATE_PLAYER_FANTA_POINTS_ENDPOINT = "/job/points/"
    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2000
  }
}