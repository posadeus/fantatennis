package com.posadeus.fantatennis.domain.exception

data class FantaTeamCreationException(val error: String) : RuntimeException(error)