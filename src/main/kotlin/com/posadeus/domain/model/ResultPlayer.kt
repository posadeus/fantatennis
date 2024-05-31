package com.posadeus.domain.model

import com.posadeus.controller.model.player.Player

sealed interface ResultPlayer

data class FoundPlayer(val player: Player) : ResultPlayer
data object NoPlayer : ResultPlayer
data object ErrorPlayer : ResultPlayer