package com.posadeus.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.posadeus.controller.model.player.Category
import com.posadeus.controller.model.player.Country
import com.posadeus.controller.model.player.Player
import com.posadeus.controller.model.player.Result
import com.posadeus.controller.player.PlayerController
import com.posadeus.domain.model.ErrorPlayer
import com.posadeus.domain.model.FoundPlayer
import com.posadeus.domain.model.NoPlayer
import com.posadeus.domain.service.player.PlayerService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class PlayerControllerTest {

  private val service: PlayerService = mockk()

  private val controller: PlayerApi = PlayerController(service)

  private val objectMapper = ObjectMapper()
  private val mvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
      .build()

  @Test
  fun `200 response`() {

    val expected = aPlayerWith(ANY_ID)
    val player = FoundPlayer(expected)

    every { service.getPlayer(ANY_ID) } returns player

    mvc.perform(MockMvcRequestBuilders.get(PLAYER_ENDPOINT + ANY_ID)
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().json(toJson(expected)))
  }

  @Test
  fun `404 response`() {

    val player = NoPlayer

    every { service.getPlayer(ANY_ID) } returns player

    mvc.perform(MockMvcRequestBuilders.get(PLAYER_ENDPOINT + ANY_ID)
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
  }

  @Test
  fun `500 response`() {

    val player = ErrorPlayer

    every { service.getPlayer(ANY_ID) } returns player

    mvc.perform(MockMvcRequestBuilders.get(PLAYER_ENDPOINT + ANY_ID)
                    .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isInternalServerError)
  }

  private fun aPlayerWith(id: Int): Player =
      Player(id = id,
             country = Country("", ""),
             single = Category(Result(bestRank = 0), Result(0)),
             double = Category(Result(bestRank = 0), Result(0)))

  @Throws(JsonProcessingException::class)
  private fun toJson(obj: Any): String =
      objectMapper.writeValueAsString(obj)

  companion object {

    private const val PLAYER_ENDPOINT = "/player/"
    private const val ANY_ID = 1
  }
}