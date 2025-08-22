package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.FantaTeamCreationException
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.FantaTeam
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcCreateTeamRepository(private val jdbcTemplate: JdbcTemplate,
                               private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : CreateTeamRepository {

  @Transactional
  override fun create(ownerId: String, fantaTournamentId: Int): FantaTeam =
      try {

        verifyExistenceOf(fantaTournamentId)
        val fantaTeamId = createFantaTeamsRecord(ownerId)
        createFantaTournamentsTeamsRecord(fantaTournamentId, fantaTeamId)

        FantaTeam(id = fantaTeamId, ownerId = ownerId)
      }
      catch (e: Exception) {

        throw FantaTeamCreationException("Error during DB operation ${e.message}")
      }

  private fun verifyExistenceOf(fantaTournamentId: Int) {

      namedParameterJdbcTemplate.queryForObject(COUNT_FANTA_TOURNAMENT_QUERY, mapOf("id" to fantaTournamentId), Long::class.java)
      ?: throw IllegalArgumentException("Fanta Tournament $fantaTournamentId not found")
  }

  private fun createFantaTeamsRecord(ownerId: String): Int {

    val keyHolder = GeneratedKeyHolder()
    val rows = namedParameterJdbcTemplate.update(CREATE_FANTA_TEAMS_QUERY,
                                                 MapSqlParameterSource().addValue("ownerId", ownerId),
                                                 keyHolder,
                                                 arrayOf("TEAM_ID"))

    if (rows == 0 || keyHolder.key == null)
      throw NoInsertException("Insert failed or no key generated on FANTA_TEAMS")

    return keyHolder.key!!.toInt()
  }

  private fun createFantaTournamentsTeamsRecord(fantaTournamentId: Int, fantaTeamId: Int) {

    jdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, fantaTournamentId, fantaTeamId)
  }

  companion object {

    private val COUNT_FANTA_TOURNAMENT_QUERY = """
      SELECT COUNT(*)
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()

    private val CREATE_FANTA_TEAMS_QUERY = """
      INSERT INTO FANTA_TEAMS
      (OWNER_ID)
      VALUES(:ownerId);
    """.trimIndent()

    private val CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS_TEAMS
      (FANTA_TOURNAMENT_ID, TEAM_ID)
      VALUES(?, ?);
    """.trimIndent()
  }
}
