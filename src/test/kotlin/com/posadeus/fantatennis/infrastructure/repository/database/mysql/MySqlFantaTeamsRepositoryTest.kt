package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.FantaTeamError
import com.posadeus.fantatennis.domain.model.FantaTeamOk
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MySqlFantaTeamsRepositoryTest {

  private val fantaTeamsDao: FantaTeamsDao = mockk()
  private val fantaTournamentsTeamsDao: FantaTournamentsTeamsDao = mockk()

  private val repository: FantaTeamsRepository = MySqlFantaTeamsRepository(fantaTeamsDao,
                                                                           fantaTournamentsTeamsDao)

  @Nested
  inner class CreateTeam {

    @Test
    fun `team is created`() {

      val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                                 startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                 endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                 tournamentYear = A_TOURNAMENT_YEAR)
      val toCreateFantaTeamsEntity = FantaTeamsEntity(ownerId = AN_OWNER_ID)
      val createdFantaTeamsEntity = FantaTeamsEntity(teamId = 1, ownerId = AN_OWNER_ID)
      val fantaTournamentsEntity = FantaTournamentsEntity(id = A_TOURNAMENT_ID,
                                                          startingTournament = A_STARTING_TOURNAMENT_ID,
                                                          endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                          year = A_TOURNAMENT_YEAR)
      val fantaTournamentsTeamsEntity =
        FantaTournamentsTeamsEntity(id = FantaTournamentsTeamsKeyEmbedded(tournamentId = A_TOURNAMENT_ID, teamId = 1),
                                    tournament = fantaTournamentsEntity,
                                    fantaTeam = createdFantaTeamsEntity)

      val expected = FantaTeamOk(id = 1, ownerId = AN_OWNER_ID)

      every { fantaTeamsDao.save(toCreateFantaTeamsEntity) } returns createdFantaTeamsEntity
      every { fantaTeamsDao.flush() } just runs
      every { fantaTournamentsTeamsDao.save(fantaTournamentsTeamsEntity) } returns fantaTournamentsTeamsEntity

      assertThat(repository.createTeam(AN_OWNER_ID, fantaTournament)).isEqualTo(expected)
    }

    @Test
    fun `error during creation - fantaTeamsDao`() {

      val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                                 startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                 endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                 tournamentYear = A_TOURNAMENT_YEAR)
      val toCreateFantaTeamsEntity = FantaTeamsEntity(ownerId = AN_OWNER_ID)

      val expected = FantaTeamError

      every { fantaTeamsDao.save(toCreateFantaTeamsEntity) } throws Exception()

      assertThat(repository.createTeam(AN_OWNER_ID, fantaTournament)).isEqualTo(expected)

      verify { fantaTournamentsTeamsDao wasNot called }
    }

    @Test
    fun `error during creation - fantaTournamentsTeamsDao`() {

      val fantaTournament = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                                 startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                 endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                 tournamentYear = A_TOURNAMENT_YEAR)
      val toCreateFantaTeamsEntity = FantaTeamsEntity(ownerId = AN_OWNER_ID)
      val createdFantaTeamsEntity = FantaTeamsEntity(teamId = 1, ownerId = AN_OWNER_ID)
      val fantaTournamentsEntity = FantaTournamentsEntity(id = A_TOURNAMENT_ID,
                                                          startingTournament = A_STARTING_TOURNAMENT_ID,
                                                          endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                          year = A_TOURNAMENT_YEAR)
      val fantaTournamentsTeamsEntity =
        FantaTournamentsTeamsEntity(id = FantaTournamentsTeamsKeyEmbedded(tournamentId = A_TOURNAMENT_ID, teamId = 1),
                                    tournament = fantaTournamentsEntity,
                                    fantaTeam = createdFantaTeamsEntity)

      val expected = FantaTeamError

      every { fantaTeamsDao.save(toCreateFantaTeamsEntity) } returns createdFantaTeamsEntity
      every { fantaTeamsDao.flush() } just runs
      every { fantaTournamentsTeamsDao.save(fantaTournamentsTeamsEntity) } throws Exception()

      assertThat(repository.createTeam(AN_OWNER_ID, fantaTournament)).isEqualTo(expected)
    }
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_TOURNAMENT_ID = 111
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2222
  }
}