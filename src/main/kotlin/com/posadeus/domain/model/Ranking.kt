package com.posadeus.domain.model

import com.posadeus.controller.model.ranking.RankedPlayer

sealed interface Ranking

data class RankedPlayers(val players: List<RankedPlayer>) : Ranking
data object EmptyRanking : Ranking