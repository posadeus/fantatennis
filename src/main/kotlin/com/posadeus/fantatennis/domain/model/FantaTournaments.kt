package com.posadeus.fantatennis.domain.model

sealed interface FantaTournaments {

  data class Valid(val tournaments: Set<FantaTournament.ValidFantaTournament>) : FantaTournaments

  data object Invalid : FantaTournaments
}
