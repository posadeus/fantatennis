package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.player.Player

sealed interface ResultPlayer

data class FoundPlayer(val player: Player) : ResultPlayer
data object NoPlayer : ResultPlayer
data object ErrorPlayer : ResultPlayer