package com.posadeus.fantatennis.infrastructure.repository.exception

data class InvalidPlayersSwapException(val error: String) : RuntimeException(error)
