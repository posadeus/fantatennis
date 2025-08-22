package com.posadeus.fantatennis.domain.exception

data class InvalidAddPlayersException(val error: String) : RuntimeException(error)