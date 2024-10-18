package com.posadeus.fantatennis.domain.model

sealed interface FantaTeam

data class FantaTeamOk(val id: Int,
                       val ownerId: String) : FantaTeam

data object FantaTeamError : FantaTeam