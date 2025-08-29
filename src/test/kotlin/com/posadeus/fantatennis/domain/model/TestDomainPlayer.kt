package com.posadeus.fantatennis.domain.model

import java.math.BigDecimal

object TestDomainPlayer {

  fun aDomainPlayer(id: String = "",
                    atpId: String = "",
                    fullName: String = "",
                    rolandGarrosId: BigDecimal? = null): DomainPlayer =
      DomainPlayer(id = id,
                   atpId = atpId,
                   fullName = fullName,
                   rolandGarrosId = rolandGarrosId)
}