package com.posadeus.fantatennis.controller.ranking

import com.posadeus.fantatennis.controller.RankingApi
import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.model.EmptyRanking
import com.posadeus.fantatennis.domain.model.RankedPlayers
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RankingController(private val service: RankingService) : RankingApi {

  override fun players(positions: Int): ResponseEntity<List<RankedPlayerDto>> =
      when (val rankedPlayer = service.retrieveRankedPlayer(positions)) {

        is RankedPlayers -> ResponseEntity.ok(rankedPlayer.players)
        is EmptyRanking -> ResponseEntity.internalServerError().build()
      }
}
