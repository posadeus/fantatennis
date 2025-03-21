package com.posadeus.fantatennis.domain.model

object TestDomainPlayer {

  fun aDomainPlayer(id: String = "",
                    atpId: String = "",
                    fullName: String = ""): DomainPlayer =
      DomainPlayer(id = id,
                   atpId = atpId,
                   fullName = fullName)
}