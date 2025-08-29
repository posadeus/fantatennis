package com.posadeus.fantatennis.domain.model

import java.math.BigDecimal

data class DomainPlayer(val id: String,
                        val atpId: String,
                        val fullName: String,
                        val rolandGarrosId: BigDecimal? = null)
