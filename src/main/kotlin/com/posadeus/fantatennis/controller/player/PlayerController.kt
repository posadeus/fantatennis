package com.posadeus.fantatennis.controller.player

import com.posadeus.fantatennis.controller.PlayerApi
import com.posadeus.fantatennis.controller.model.player.Player
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.player.PlayerService
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
