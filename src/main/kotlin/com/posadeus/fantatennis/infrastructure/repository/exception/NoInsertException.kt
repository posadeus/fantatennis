package com.posadeus.fantatennis.infrastructure.repository.exception

data class NoInsertException(override val message: String) : RuntimeException(message)
