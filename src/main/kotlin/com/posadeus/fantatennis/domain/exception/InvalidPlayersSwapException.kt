package com.posadeus.fantatennis.domain.exception

data class InvalidPlayersSwapException(val error: String) : RuntimeException(error)
