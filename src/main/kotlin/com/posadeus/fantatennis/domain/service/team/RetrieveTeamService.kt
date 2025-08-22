package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.Team

class RetrieveTeamService(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository) {

  fun retrieve(teamId: Int): Team =
      retrieveFantaTeamRepository.retrieve(teamId)
}
