package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.exception.FantaTeamCreationException
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.CurrentUser
import com.posadeus.fantatennis.domain.model.*
import org.slf4j.LoggerFactory

class CreateTeamService(private val createTeamRepository: CreateTeamRepository,
                        private val currentUser: CurrentUser) {

  fun create(dto: TeamToCreateDto): TeamCreation =
      try {
        createTeamRepository.create(currentUser.ownerId(), dto.tournamentId)
          .let(::toTeamCreated)
      }
      catch (e: FantaTeamCreationException) {

        ErrorTeamCreation.also { LOGGER.error(e.message) }
      }
      catch (e: IllegalStateException) {

        ErrorTeamCreation.also { LOGGER.error(e.message) }
      }

  private fun toTeamCreated(fantaTeam: FantaTeam): TeamCreated =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(CreateTeamService::class.java)
  }
}
