package com.posadeus.fantatennis.domain.model

sealed interface AddPlayers

data class AddPlayersOk(val players: Set<DomainPlayer>) : AddPlayers
data class PlayersNotFound(val missingPlayerIds: Set<String>) : AddPlayers
data object AddPlayersTeamNotFound : AddPlayers
data object AddPlayersError : AddPlayers

