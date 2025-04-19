package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.*
import com.posadeus.fantatennis.domain.model.FantaTournaments
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.FantaTournamentDto
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcRetrieveAllFantaTournamentsRepository(private val jdbcTemplate: JdbcTemplate) : RetrieveAllFantaTournamentsRepository {

  override fun retrieve(): FantaTournaments =
      try {

        jdbcTemplate.query(RETRIEVE_QUERY, rowMapper)
            .map(::toValidFantaTournament)
            .let(List<ValidFantaTournament>::toSet)
            .let(::Valid)
      }
      catch (e: Exception) {

        LOGGER.error("Error retrieving fanta tournaments", e)
        Invalid
      }

  private val rowMapper = RowMapper { rs, _ ->
    FantaTournamentDto(id = rs.getInt("FANTA_TOURNAMENT_ID"),
                       startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                       endingTournamentId = rs.getInt("ENDING_TOURNAMENT"),
                       year = rs.getInt("TOURNAMENT_YEAR"))
  }

  private fun toValidFantaTournament(dto: FantaTournamentDto): ValidFantaTournament =
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
