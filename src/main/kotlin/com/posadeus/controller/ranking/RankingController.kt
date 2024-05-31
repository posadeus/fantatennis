package com.posadeus.controller.ranking

import com.posadeus.controller.RankingApi
import com.posadeus.controller.model.ranking.RankedPlayer
import com.posadeus.domain.service.ranking.RankingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RankingController(private val service: RankingService) : RankingApi {

  override fun players(): ResponseEntity<List<RankedPlayer>> =
      ResponseEntity.ok(service.retrieveRankedPlayer())
}
