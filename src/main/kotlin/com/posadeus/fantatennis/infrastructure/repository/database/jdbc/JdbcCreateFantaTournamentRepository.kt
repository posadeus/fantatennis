package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.NewJdbcFantaTournamentDto
import org.slf4j.LoggerFactory

class JdbcCreateFantaTournamentRepository(private val fantaTournamentDao: FantaTournamentDao) : CreateFantaTournamentRepository {

  override fun create(dto: FantaTournamentToCreateDto): FantaTournament =
      try {

        NewJdbcFantaTournamentDto(dto.startingTournamentId, dto.endingTournamentId, dto.tournamentYear)
            .let(fantaTournamentDao::persist)
            .let { toValidFantaTournament(it, dto) }
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error while interacting with database", e)
        InvalidFantaTournament
      }

  private fun toValidFantaTournament(it: Int, dto: FantaTournamentToCreateDto) =
      ValidFantaTournament(id = it,
                           startingTournamentId = dto.startingTournamentId,
                           endingTournamentId = dto.endingTournamentId,
                           tournamentYear = dto.tournamentYear)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcCreateFantaTournamentRepository::class.java)
  }
}