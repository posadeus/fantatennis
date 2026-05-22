package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam

class RetrieveTeamService(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                          private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository,
                          private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository) {

  fun retrieve(teamId: Int): Team =
      when (retrieveFantaTeamRepository.retrieveByTeamId(teamId)) {
        is NotFoundDomainTeam -> TeamIdNotFoundTeam
        is FoundDomainTeam -> ErrorTeam
      }
}
