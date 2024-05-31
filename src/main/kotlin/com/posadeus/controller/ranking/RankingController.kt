package com.posadeus.controller.ranking

import com.posadeus.controller.RankingApi
import com.posadeus.controller.model.ranking.RankedPlayer
import com.posadeus.domain.model.EmptyRanking
import com.posadeus.domain.model.RankedPlayers
import com.posadeus.domain.service.ranking.RankingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RankingController(private val service: RankingService) : RankingApi {

  override fun players(): ResponseEntity<List<RankedPlayer>> =
      when (val rankedPlayer = service.retrieveRankedPlayer()) {

        is RankedPlayers -> ResponseEntity.ok(rankedPlayer.players)
        is EmptyRanking -> ResponseEntity.internalServerError().build()
      }
}
