package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidTournamentException
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto.Companion.tournamentRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcTournamentDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : TournamentDao {

  override fun retrieveAllBy(year: Int): List<JdbcTournamentDto> =
      namedParameterJdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, mapOf("year" to year), tournamentRowMapper)

  override fun retrieveBy(id: Int): JdbcTournamentDto =
      namedParameterJdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, mapOf("id" to id), tournamentRowMapper)
      ?: throw EmptyResultDataAccessException("No tournament found with id $id", 1)

  @Transactional
  override fun persistNewTournaments(tournaments: List<JdbcTournamentDto>) {
    try {

      val batchResult = tournaments
          .map(::toEntryParams)
          .let(::persistNewTournaments)

      if (batchResult.any { it != 1 })
        throw InvalidTournamentException(error = "Tournaments [${manageError(tournaments, batchResult) { it.atpTourId.toString() }}] not inserted, operation reverted.")
    }
    catch (e: RuntimeException) {

      throw InvalidTournamentException(error = "Unexpected error during insert: ${e.message}")
    }
  }

  private fun persistNewTournaments(params: List<Map<String, Any>>): IntArray =
      namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, params.toTypedArray())

  private fun toEntryParams(dto: JdbcTournamentDto): Map<String, Any> =
      mapOf("atpTourId" to dto.atpTourId,
            "tennisTvId" to dto.tennisTvId,
            "name" to dto.name,
            "points" to dto.points,
            "location" to dto.location,
            "surface" to dto.surface,
            "year" to dto.year,
            "startDate" to dto.startDate,
            "endDate" to dto.endDate)

  companion object {

    private val RETRIEVE_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS
      WHERE `YEAR` = :year;
    """.trimIndent()

    private val RETRIEVE_TOURNAMENT_QUERY = """
      SELECT *
      FROM TOURNAMENTS
      WHERE TOURNAMENT_ID = :id;
    """.trimIndent()

    private val INSERT_TOURNAMENTS_QUERY = """
      INSERT INTO TOURNAMENTS
      (ATP_TOUR_ID, TENNIS_TV_ID, NAME, POINTS, LOCATION, SURFACE, `YEAR`, START_DATE, END_DATE)
      VALUES(:atpTourId, :tennisTvId, :name, :points, :location, :surface, :year, :startDate, :endDate);
    """.trimIndent()
  }
}
