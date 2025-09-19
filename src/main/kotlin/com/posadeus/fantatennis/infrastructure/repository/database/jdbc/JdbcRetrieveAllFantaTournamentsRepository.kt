package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.*
import com.posadeus.fantatennis.domain.model.FantaTournaments
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto.Companion.fantaTournamentRowMapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate

class JdbcRetrieveAllFantaTournamentsRepository(private val jdbcTemplate: JdbcTemplate) : RetrieveAllFantaTournamentsRepository {

  override fun retrieve(): FantaTournaments =
      try {

        jdbcTemplate.query(RETRIEVE_QUERY, fantaTournamentRowMapper)
            .map(::toValidFantaTournament)
            .let(List<ValidFantaTournament>::toSet)
            .let(::Valid)
      }
      catch (e: Exception) {

        LOGGER.error("Error retrieving fanta tournaments", e)
        Invalid
      }

  private fun toValidFantaTournament(dto: JdbcFantaTournamentDto): ValidFantaTournament =
      ValidFantaTournament(id = dto.id,
                           startingTournamentId = dto.startingTournamentId,
                           endingTournamentId = dto.endingTournamentId,
                           tournamentYear = dto.year)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrieveAllFantaTournamentsRepository::class.java)

    private val RETRIEVE_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS
    """.trimIndent()
  }
}
