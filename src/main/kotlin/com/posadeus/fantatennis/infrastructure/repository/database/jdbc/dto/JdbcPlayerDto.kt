package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper
import java.math.BigDecimal

data class JdbcPlayerDto(val playerId: String,
                         val atpTourId: String,
                         val fullName: String,
                         val rolandGarrosId: BigDecimal? = null) {

  companion object {

    val playerRowMapper = RowMapper { rs, _ ->
      JdbcPlayerDto(playerId = rs.getString("PLAYER_ID"),
                    atpTourId = rs.getString("ATP_TOUR_ID"),
                    fullName = rs.getString("FULL_NAME"),
                    rolandGarrosId = rs.getObject("RG_ID", BigDecimal::class.java))
    }
  }
}
