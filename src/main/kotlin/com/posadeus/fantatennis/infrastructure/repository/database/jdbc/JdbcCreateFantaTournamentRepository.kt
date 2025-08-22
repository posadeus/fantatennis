package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate

class JdbcCreateFantaTournamentRepository(private val jdbcTemplate: JdbcTemplate) : CreateFantaTournamentRepository {

  override fun create(dto: TournamentToCreateDto): FantaTournament =
      try {

        jdbcTemplate.update(CREATE_FANTA_TOURNAMENT_QUERY,
                            dto.startingTournamentId,
                            dto.endingTournamentId,
                            dto.tournamentYear)
            .let {
              ValidFantaTournament(id = it,
                                   startingTournamentId = dto.startingTournamentId,
                                   endingTournamentId = dto.endingTournamentId,
                                   tournamentYear = dto.tournamentYear)
            }
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error while interacting with database", e)
        InvalidFantaTournament
      }

  companion object {

    private val CREATE_FANTA_TOURNAMENT_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS
        (STARTING_TOURNAMENT, ENDING_TOURNAMENT, TOURNAMENT_YEAR)
      VALUES(?, ?, ?);
    """.trimIndent()

    private val LOGGER = LoggerFactory.getLogger(JdbcCreateFantaTournamentRepository::class.java)
  }
}