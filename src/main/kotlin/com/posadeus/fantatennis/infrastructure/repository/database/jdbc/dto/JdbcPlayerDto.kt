package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import java.math.BigDecimal

data class JdbcPlayerDto(val playerId: String,
                         val atpTourId: String,
                         val fullName: String,
                         val rolandGarrosId: BigDecimal? = null)
