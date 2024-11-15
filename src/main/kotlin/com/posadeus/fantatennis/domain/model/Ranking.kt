package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto

sealed interface Ranking

data class RankedPlayers(val players: List<RankedPlayerDto>) : Ranking
data object EmptyRanking : Ranking