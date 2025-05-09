package com.posadeus.fantatennis.domain.model

sealed interface AddPlayers {

  data class ValidAddPlayers(val players: Set<DomainPlayer>) : AddPlayers

  sealed interface InvalidAddPlayers : AddPlayers {

    data class PlayersNotFound(val missingPlayerIds: Set<String>) : InvalidAddPlayers
    data object AddPlayersTeamNotFound : InvalidAddPlayers
    data object AddPlayersTournamentNotFound : InvalidAddPlayers
    data class AddPlayersException(val error: String) : InvalidAddPlayers, RuntimeException(error)
  }
}

