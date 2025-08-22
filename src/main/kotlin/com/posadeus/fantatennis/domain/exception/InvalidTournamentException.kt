package com.posadeus.fantatennis.domain.exception

class InvalidTournamentException(val error: String) : RuntimeException(error)
