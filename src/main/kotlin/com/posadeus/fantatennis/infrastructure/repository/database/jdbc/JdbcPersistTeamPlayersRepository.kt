package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.PersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcPersistTeamPlayersRepository(private val teamDao: TeamDao) : PersistTeamPlayersRepository {

  @Transactional
  override fun persist(teamId: Int, playerIds: Set<String>, startingTournamentId: Int) {

    try {

      playerIds
          .map { toJdbcTeamDto(teamId, it, startingTournamentId) }
          .toSet()
          .let(teamDao::persist)
    }
    catch (e: InvalidAddPlayersException) {

      throw InvalidAddPlayersException(error = "Unexpected error during insert: ${e.message} - Operation reverted.")
    }
  }

  private fun toJdbcTeamDto(teamId: Int,
                            player: String,
                            startingTournamentId: Int) =
      JdbcTeamDto(teamId = teamId,
                  playerId = player,
                  startingTournamentId = startingTournamentId,
                  endingTournamentId = null)
}
