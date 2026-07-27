package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.Tournament.*
import org.slf4j.LoggerFactory

class AddPlayersTeamService(private val persistTeamPlayersRepository: PersistTeamPlayersRepository,
                            private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                            private val retrieveTournamentsRepository: RetrieveTournamentsRepository,
                            private val retrievePlayersRepository: RetrievePlayersRepository) {

  // TODO Add validation: if a player is already present and without an endingTournamentId before the new startingTournamentId, it cannot be added
  fun addPlayers(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): Team =
      when (retrieveFantaTeamRepository.retrieveByTeamId(teamId)) {

        is FoundDomainTeam ->
          when (retrieveTournamentsRepository.retrieveBy(startingTournamentId)) {

            is FoundTournament ->
              try {

                val allPlayers = retrievePlayersRepository.retrieve()
                val allPlayerIds = allPlayers.map { it.id }
                val foundPlayerIds = playerIds.filter { it in allPlayerIds }

                if (foundPlayerIds.size != playerIds.size) {

                  ErrorTeam.also { LOGGER.error("Players not found") }
                }
                else {

                  persistTeamPlayersRepository.persist(teamId, playerIds, startingTournamentId)

                  allPlayers
                      .filter { it.id in foundPlayerIds }
                      .toSet()
                      .let(::toFoundTeam)
                }
              }
              catch (e: InvalidAddPlayersException) {

                ErrorTeam.also { LOGGER.error(e.message) }
              }

            is NotFoundTournament, InternalErrorTournament -> ErrorTeam.also { LOGGER.error("Tournament not found: $startingTournamentId") } // FIXME: not a generic error
          }

        is NotFoundDomainTeam -> TeamIdNotFoundTeam
      }

  private fun toFoundTeam(players: Set<DomainPlayer>): FoundTeam =
      players
          .map { PlayerPointsDto(fullName = it.fullName, fantaPoints = 0.0) }
          .let { FoundTeam(team = TeamDto(players = it, totalScore = 0.0)) }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(AddPlayersTeamService::class.java)
  }
}
