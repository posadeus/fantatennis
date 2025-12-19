package com.posadeus.fantatennis.domain.model

sealed interface Tournament {

  data class FoundTournament(val id: Int,
                             val tennisTvId: Int,
                             val name: String,
                             val points: Int,
                             val year: Int) : Tournament

  data object NotFoundTournament : Tournament
}