package com.posadeus.controller.player

import com.posadeus.controller.PlayerApi
import com.posadeus.controller.model.player.Player
import com.posadeus.domain.model.ErrorPlayer
import com.posadeus.domain.model.FoundPlayer
import com.posadeus.domain.model.NoPlayer
import com.posadeus.domain.service.player.PlayerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PlayerController(private val service: PlayerService) : PlayerApi {

  override fun player(id: Int): ResponseEntity<Player> =
      when (val player = service.getPlayer(id)) {

        is FoundPlayer -> ResponseEntity.ok(player.player)
        is NoPlayer -> ResponseEntity.notFound().build()
        is ErrorPlayer -> ResponseEntity.internalServerError().build()
      }
}
