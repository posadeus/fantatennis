package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidTournamentException
import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.domain.model.TournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcPersistTournamentsRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : PersistTournamentsRepository {

  @Transactional
  override fun persistAll(tournaments: FoundTournamentsRegistry) {
    try {

      val batchResult = tournaments.tournaments
          .map(::toEntryParams)
          .let(::persistAll)

      if (batchResult.any { it != 1 })
        throw InvalidTournamentException(error = "Tournaments [${manageError(tournaments.tournaments, batchResult) { it.atpTourId.toString() }}] not inserted, operation reverted.")
    }
    catch (e: RuntimeException) {

      throw InvalidTournamentException(error = "Unexpected error during insert: ${e.message}")
    }
  }

  private fun persistAll(params: List<Map<String, Any>>): IntArray =
      namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, params.toTypedArray())

  private fun toEntryParams(tournamentRegistry: TournamentRegistry): Map<String, Any> =
      mapOf("atpTourId" to tournamentRegistry.atpTourId,
            "tennisTvId" to tournamentRegistry.tennisTvId,
            "name" to tournamentRegistry.name,
            "points" to tournamentRegistry.points,
            "location" to tournamentRegistry.location,
            "surface" to tournamentRegistry.surface.name,
            "year" to tournamentRegistry.year,
            "startDate" to tournamentRegistry.startDate,
            "endDate" to tournamentRegistry.endDate)

  companion object {

    private val INSERT_TOURNAMENTS_QUERY = """
      INSERT INTO TOURNAMENTS
      (ATP_TOUR_ID, TENNIS_TV_ID, NAME, POINTS, LOCATION, SURFACE, `YEAR`, START_DATE, END_DATE)
      VALUES(:atpTourId, :tennisTvId, :name, :points, :location, :surface, :year, :startDate, :endDate);
    """.trimIndent()
  }
}
