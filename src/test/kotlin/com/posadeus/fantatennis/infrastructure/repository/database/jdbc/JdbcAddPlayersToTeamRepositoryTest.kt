package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.AddPlayersTeamNotFound
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.AddPlayersTournamentNotFound
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.FantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcAddPlayersToTeamRepositoryTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: AddPlayersToTeamRepository = JdbcAddPlayersToTeamRepository(jdbcTemplate)

  @Test
  fun `fanta team not found`() {

    val fantaTeamsQueryParams = mapOf("teamId" to 1)

    val expected = AddPlayersTeamNotFound

    // FIXME improve logic with queryForObject
    every {
      jdbcTemplate.query(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } returns emptyList<FantaTeamDto>()

    assertThat(repository.add(1, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `tournament not found`() {

    val fantaTeamsQueryParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = FantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val tournamentQueryParams = mapOf("tournamentId" to 1234)

    val expected = AddPlayersTournamentNotFound

    every {
      jdbcTemplate.query(RETRIEVE_FANTA_TEAM_QUERY, fantaTeamsQueryParams, any<RowMapper<FantaTeamDto>>())
    } returns listOf(fantaTeam)
    every {
      jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, tournamentQueryParams, any<RowMapper<JdbcTournamentDto>>())
    } throws EmptyResultDataAccessException(1)

    assertThat(repository.add(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), 1234)).isEqualTo(expected)
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1234

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()

    private val RETRIEVE_TOURNAMENT_QUERY = """
      SELECT *
      FROM TOURNAMENTS 
      WHERE TOURNAMENT_ID = :tournamentId;
    """.trimIndent()
  }
}