package com.posadeus.fantatennis.domain.exception

class InvalidPlayerPointsException(val error: String) : RuntimeException(error)