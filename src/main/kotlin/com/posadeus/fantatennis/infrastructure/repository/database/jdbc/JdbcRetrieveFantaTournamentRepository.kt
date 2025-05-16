package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTournamentRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : RetrieveFantaTournamentRepository {

  override fun retrieve(fantaTournamentId: Int): FantaTournament =
      try {

        jdbcTemplate.queryForObject(RETRIEVE_QUERY, mapOf("id" to fantaTournamentId), rowMapper)
            .let(::toValidFantaTournament)
      }
      catch (e: EmptyResultDataAccessException) {

        LOGGER.error("Fanta Tournament $fantaTournamentId not found")
        InvalidFantaTournament
      }
      catch (e: Exception) {

        LOGGER.error("Error during DB operation ${e.message}")
        InvalidFantaTournament
      }

  private fun toValidFantaTournament(dto: JdbcFantaTournamentDto?): ValidFantaTournament =
      ValidFantaTournament(id = dto!!.id,
                           startingTournamentId = dto.startingTournamentId,
                           endingTournamentId = dto.endingTournamentId,
                           tournamentYear = dto.year)

  private val rowMapper = RowMapper { rs, _ ->
    JdbcFantaTournamentDto(id = rs.getInt("FANTA_TOURNAMENT_ID"),
                           startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                           endingTournamentId = rs.getInt("ENDING_TOURNAMENT"),
                           year = rs.getInt("TOURNAMENT_YEAR"))
  }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrieveFantaTournamentRepository::class.java)

    private val RETRIEVE_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()
  }
}
