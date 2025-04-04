package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FoundFantaTournamentResults
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentResultsDto
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Optional.empty
import java.util.Optional.of

class MySqlFantaTournamentsRepositoryTest {

  private val fantaTournamentsDao: FantaTournamentsDao = mockk()

  private val repository: FantaTournamentsRepository = MySqlFantaTournamentsRepository(fantaTournamentsDao)

  @Nested
  inner class RetrieveFantaTournament {

    @Test
    fun `retrieve found a result`() {

      val fantaTournamentsEntity = FantaTournamentsEntity(id = A_TOURNAMENT_ID,
                                                          startingTournament = A_STARTING_TOURNAMENT_ID,
                                                          endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                          year = A_TOURNAMENT_YEAR)

      val expected = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                          startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                          tournamentYear = A_TOURNAMENT_YEAR)

      every { fantaTournamentsDao.findById(A_TOURNAMENT_ID) } returns of(fantaTournamentsEntity)

      assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `retrieve found no results`() {

      val expected = InvalidFantaTournament

      every { fantaTournamentsDao.findById(A_TOURNAMENT_ID) } returns empty()

      assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `error during retrieve operation`() {

      val expected = InvalidFantaTournament

      every { fantaTournamentsDao.findById(A_TOURNAMENT_ID) } throws Exception()

      assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveFantaTournamentResults {

    @Test
    fun `retrieve results successfully`() {

      val tournamentResultsDto1 = TournamentResultsDtoImpl(tournamentId = A_TOURNAMENT_ID,
                                                           teamId = A_TEAM_ID,
                                                           ownerId = AN_OWNER_ID,
                                                           playerId = A_PLAYER_ID,
                                                           playerFullName = A_PLAYER_FULL_NAME,
                                                           playerTotalScore = 2.00)
      val tournamentResultsDto2 = TournamentResultsDtoImpl(tournamentId = A_TOURNAMENT_ID,
                                                           teamId = A_TEAM_ID,
                                                           ownerId = AN_OWNER_ID,
                                                           playerId = ANOTHER_PLAYER_ID,
                                                           playerFullName = ANOTHER_PLAYER_FULL_NAME,
                                                           playerTotalScore = 1.00)
      val tournamentResultsDto3 = TournamentResultsDtoImpl(tournamentId = A_TOURNAMENT_ID,
                                                           teamId = ANOTHER_TEAM_ID,
                                                           ownerId = ANOTHER_OWNER_ID,
                                                           playerId = A_THIRD_PLAYER_ID,
                                                           playerFullName = A_THIRD_PLAYER_FULL_NAME,
                                                           playerTotalScore = 4.00)
      val tournamentResultsDto = listOf(tournamentResultsDto1, tournamentResultsDto2, tournamentResultsDto3)

      val aPlayer = TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 2.00)
      val anotherPlayer = TeamPlayerDto(fullName = ANOTHER_PLAYER_FULL_NAME, fantaPoints = 1.00)
      val aTeam = TeamDto(owner = AN_OWNER_ID, players = listOf(aPlayer, anotherPlayer), totalScore = 3.00)
      val aThirdPlayer = TeamPlayerDto(fullName = A_THIRD_PLAYER_FULL_NAME, fantaPoints = 4.00)
      val anotherTeam = TeamDto(owner = ANOTHER_OWNER_ID, players = listOf(aThirdPlayer), totalScore = 4.00)
      val expected = FoundFantaTournamentResults(TournamentDto(teams = listOf(anotherTeam, aTeam)))

      every { fantaTournamentsDao.findTournamentResultsByTournamentId(A_TOURNAMENT_ID) } returns tournamentResultsDto

      assertThat(repository.retrieveTournamentResults(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveAllFantaTournaments {

    @Test
    fun `retrieve all successfully`() {

      val fantaTournamentsEntity = FantaTournamentsEntity(id = A_TOURNAMENT_ID,
                                                          startingTournament = A_STARTING_TOURNAMENT_ID,
                                                          endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                          year = A_TOURNAMENT_YEAR)
      val anotherFantaTournamentsEntity = FantaTournamentsEntity(id = ANOTHER_TOURNAMENT_ID,
                                                                 startingTournament = ANOTHER_STARTING_TOURNAMENT_ID,
                                                                 endingTournament = ANOTHER_ENDING_TOURNAMENT_ID,
                                                                 year = ANOTHER_TOURNAMENT_YEAR)

      val expected = listOf(ValidFantaTournament(id = A_TOURNAMENT_ID,
                                                 startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                 endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                 tournamentYear = A_TOURNAMENT_YEAR),
                            ValidFantaTournament(id = ANOTHER_TOURNAMENT_ID,
                                                 startingTournamentId = ANOTHER_STARTING_TOURNAMENT_ID,
                                                 endingTournamentId = ANOTHER_ENDING_TOURNAMENT_ID,
                                                 tournamentYear = ANOTHER_TOURNAMENT_YEAR))

      every { fantaTournamentsDao.findAll() } returns listOf(fantaTournamentsEntity, anotherFantaTournamentsEntity)

      assertThat(repository.retrieveAll()).isEqualTo(expected)
    }

    @Test
    fun `retrieve all is empty`() {

      val expected = emptyList<FantaTournament>()

      every { fantaTournamentsDao.findAll() } returns emptyList()

      assertThat(repository.retrieveAll()).isEqualTo(expected)
    }
  }

  inner class TournamentResultsDtoImpl(private val tournamentId: Int,
                                       private val teamId: Int,
                                       private val ownerId: String,
                                       private val playerId: String,
                                       private val playerFullName: String,
                                       private val playerTotalScore: Double) : TournamentResultsDto {

    override fun getTournamentId(): Int = tournamentId

    override fun getTeamId(): Int = teamId

    override fun getOwnerId(): String = ownerId

    override fun getPlayerId(): String = playerId

    override fun getPlayerFullName(): String = playerFullName

    override fun getPlayerTotalScore(): Double = playerTotalScore
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val ANOTHER_TOURNAMENT_ID = 2
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2022
    private const val ANOTHER_STARTING_TOURNAMENT_ID = 2
    private const val ANOTHER_ENDING_TOURNAMENT_ID = 12
    private const val ANOTHER_TOURNAMENT_YEAR = 2023
    private const val A_TEAM_ID = 3
    private const val ANOTHER_TEAM_ID = 4
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val ANOTHER_PLAYER_FULL_NAME = "ANOTHER_PLAYER_FULL_NAME"
    private const val A_THIRD_PLAYER_FULL_NAME = "A_THIRD_PLAYER_FULL_NAME"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val ANOTHER_OWNER_ID = "ANOTHER_OWNER_ID"
  }
}