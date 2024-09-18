package com.posadeus.fantatennis.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.fantatennis.controller.model.ranking.RankedPlayer
import com.posadeus.fantatennis.controller.ranking.RankingController
import com.posadeus.fantatennis.domain.model.EmptyRanking
import com.posadeus.fantatennis.domain.model.RankedPlayers
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class RankingControllerTest {

  private val service: RankingService = mockk()

  private val controller: RankingApi = RankingController(service)

  private val objectMapper: ObjectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Test
  fun `200 response`() {

    val positions = 3
    val rankedPlayer1 = RankedPlayer(id = "123",
                                     fullName = "PLAYER1_NAME",
                                     rank = 1,
                                     points = 1000)
    val rankedPlayer2 = RankedPlayer(id = "456",
                                     fullName = "PLAYER2_NAME",
                                     rank = 2,
                                     points = 987)
    val rankedPlayer3 = RankedPlayer(id = "789",
                                     fullName = "PLAYER3_NAME",
                                     rank = 3,
                                     points = 786)
    val ranking = RankedPlayers(listOf(rankedPlayer1, rankedPlayer2, rankedPlayer3))
    val expected = listOf(rankedPlayer1, rankedPlayer2, rankedPlayer3)

    every { service.retrieveRankedPlayer() } returns ranking

    mvc.perform(MockMvcRequestBuilders.get("$RANKING_ENDPOINT/$positions")
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().json(toJson(expected)))
  }

  @Test
  fun `500 response`() {

    val ranking = EmptyRanking

    every { service.retrieveRankedPlayer() } returns ranking

    mvc.perform(MockMvcRequestBuilders.get("$RANKING_ENDPOINT/$ANY_POSITIONS")
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isInternalServerError)
  }

  @Throws(JsonProcessingException::class)
  private fun toJson(obj: Any): String =
      objectMapper.writeValueAsString(obj)

  companion object {

    private const val RANKING_ENDPOINT = "/players/ranking"
    private const val ANY_POSITIONS = 1
  }
}