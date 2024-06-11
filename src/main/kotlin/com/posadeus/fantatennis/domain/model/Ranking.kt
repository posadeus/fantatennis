package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayer

sealed interface Ranking

data class RankedPlayers(val players: List<RankedPlayer>) : Ranking
data object EmptyRanking : Ranking