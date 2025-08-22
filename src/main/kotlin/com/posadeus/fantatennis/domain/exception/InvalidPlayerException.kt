package com.posadeus.fantatennis.domain.exception

class InvalidPlayerException(val error: String) : RuntimeException(error)