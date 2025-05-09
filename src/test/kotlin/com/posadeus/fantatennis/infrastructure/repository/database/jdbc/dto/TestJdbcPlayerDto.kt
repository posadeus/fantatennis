package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

object TestJdbcPlayerDto {

  fun aJdbcPlayerDto(playerId: String = "A_PLAYER_ID",
                     atpTourId: String = "AN_ATP_TOUR_ID",
                     fullName: String = "A_FULL_NAME"): JdbcPlayerDto =
      JdbcPlayerDto(playerId = playerId,
                    atpTourId = atpTourId,
                    fullName = fullName)
}