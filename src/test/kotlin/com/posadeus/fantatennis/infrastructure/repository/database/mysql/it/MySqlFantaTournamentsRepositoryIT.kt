package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FoundFantaTournamentResults
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ContextConfiguration(classes = [com.posadeus.fantatennis.app.Application::class])
@ComponentScan(basePackages = ["com.posadeus.fantatennis.app.configuration.infrastructure.mysql"])
class MySqlFantaTournamentsRepositoryIT {

  @Autowired
  private lateinit var fantaTournamentsDao: FantaTournamentsDao

  @Autowired
  private lateinit var fantaTeamsDao: FantaTeamsDao

  @Autowired
  private lateinit var fantaTournamentsTeamsDao: FantaTournamentsTeamsDao

  @Autowired
  private lateinit var playersDao: PlayersDao

  @Autowired
  private lateinit var playersPointsDao: PlayersPointsDao

  @Autowired
  private lateinit var teamsDao: TeamsDao

  @Autowired
  private lateinit var tournamentsDao: TournamentsDao

  @Autowired
  private lateinit var mySqlFantaTournamentsRepository: FantaTournamentsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `fanta tournament retrieved`() {

    assertThat(fantaTournamentsDao.findAll()).isEmpty()

    val fantaTournamentsEntity = FantaTournamentsEntity(id = A_TOURNAMENT_ID,
                                                        startingTournament = A_STARTING_TOURNAMENT_ID,
                                                        endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                        year = A_TOURNAMENT_YEAR)

    fantaTournamentsDao.save(fantaTournamentsEntity)

    val expected = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                        startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                        endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                        tournamentYear = A_TOURNAMENT_YEAR)

    assertThat(mySqlFantaTournamentsRepository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `fanta tournament not found`() {

    assertThat(fantaTournamentsDao.findAll()).isEmpty()

    val expected = InvalidFantaTournament

    assertThat(mySqlFantaTournamentsRepository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `create fanta tournament`() {

    assertThat(fantaTournamentsDao.findAll()).isEmpty()

    val dto = TournamentToCreateDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                    endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                    tournamentYear = A_TOURNAMENT_YEAR)

    val createdEntity = FantaTournamentsEntity(id = 1,
                                               startingTournament = A_STARTING_TOURNAMENT_ID,
                                               endingTournament = AN_ENDING_TOURNAMENT_ID,
                                               year = A_TOURNAMENT_YEAR)

    val expected = ValidFantaTournament(id = 1,
                                        startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                        endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                        tournamentYear = A_TOURNAMENT_YEAR)

    assertThat(mySqlFantaTournamentsRepository.create(dto)).isEqualTo(expected)
    assertThat(fantaTournamentsDao.findAll()).containsExactly(createdEntity)
  }

  @Test
  fun `retrieve fanta tournament results`() {

    assertThat(tournamentsDao.findAll()).isEmpty()
    assertThat(playersDao.findAll()).isEmpty()
    assertThat(fantaTournamentsDao.findAll()).isEmpty()
    assertThat(fantaTeamsDao.findAll()).isEmpty()
    assertThat(fantaTournamentsTeamsDao.findAll()).isEmpty()
    assertThat(teamsDao.findAll()).isEmpty()
    assertThat(playersPointsDao.findAll()).isEmpty()

    val tournamentsEntity1 = TournamentsEntity(id = 1)
    val tournamentsEntity2 = TournamentsEntity(id = 2)
    val tournaments = listOf(tournamentsEntity1, tournamentsEntity2)
    tournamentsDao.saveAll(tournaments)

    val playersEntity1 = PlayersEntity(id = "P1", fullName = "A_FULL_NAME")
    val playersEntity2 = PlayersEntity(id = "P2", fullName = "ANOTHER_FULL_NAME")
    val playersEntity3 = PlayersEntity(id = "P3", fullName = "A_THIRD_FULL_NAME")
    val players = listOf(playersEntity1, playersEntity2, playersEntity3)
    playersDao.saveAll(players)

    val fantaTournament = FantaTournamentsEntity(id = 1, startingTournament = 1, endingTournament = 2, year = 2024)
    fantaTournamentsDao.save(fantaTournament)

    val fantaTeamsEntity1 = FantaTeamsEntity(teamId = 1)
    val fantaTeamsEntity2 = FantaTeamsEntity(teamId = 2)
    val fantaTeams = listOf(fantaTeamsEntity1, fantaTeamsEntity2)
    fantaTeamsDao.saveAll(fantaTeams)

    val fantaTournamentsTeams = listOf(FantaTournamentsTeamsEntity(id = FantaTournamentsTeamsKeyEmbedded(tournamentId = 1, teamId = 1)),
                                       FantaTournamentsTeamsEntity(id = FantaTournamentsTeamsKeyEmbedded(tournamentId = 1, teamId = 2)))
    fantaTournamentsTeamsDao.saveAll(fantaTournamentsTeams)

    val teams = listOf(TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "P1"), player = playersEntity1, fantaTeam = fantaTeamsEntity1),
                       TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "P2"), player = playersEntity2, fantaTeam = fantaTeamsEntity1),
                       TeamsEntity(id = TeamsKeyEmbedded(teamId = 2, playerId = "P3"), player = playersEntity3, fantaTeam = fantaTeamsEntity2))
    teamsDao.saveAll(teams)

    val playerPoints = listOf(PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = 2024, tournamentId = 1, playerId = "P1"),
                                                  fantaPoints = 10.00, player = playersEntity1, tournament = tournamentsEntity1),
                              PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = 2024, tournamentId = 1, playerId = "P2"),
                                                  fantaPoints = 3.00, player = playersEntity2, tournament = tournamentsEntity1),
                              PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = 2024, tournamentId = 1, playerId = "P3"),
                                                  fantaPoints = 7.00, player = playersEntity3, tournament = tournamentsEntity1),
                              PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = 2024, tournamentId = 2, playerId = "P1"),
                                                  fantaPoints = 0.00, player = playersEntity1, tournament = tournamentsEntity2),
                              PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = 2024, tournamentId = 2, playerId = "P2"),
                                                  fantaPoints = 2.00, player = playersEntity2, tournament = tournamentsEntity2),
                              PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = 2024, tournamentId = 2, playerId = "P3"),
                                                  fantaPoints = 10.00, player = playersEntity3, tournament = tournamentsEntity2))
    playersPointsDao.saveAll(playerPoints)

    val teamPlayerDto1 = TeamPlayerDto(fullName = "A_FULL_NAME", fantaPoints = 10.00)
    val teamPlayerDto2 = TeamPlayerDto(fullName = "ANOTHER_FULL_NAME", fantaPoints = 5.00)
    val teamPlayerDto3 = TeamPlayerDto(fullName = "A_THIRD_FULL_NAME", fantaPoints = 17.00)
    val teamDto1 = TeamDto(players = listOf(teamPlayerDto1, teamPlayerDto2), totalScore = 15.00)
    val teamDto2 = TeamDto(players = listOf(teamPlayerDto3), totalScore = 17.00)
    val tournament = TournamentDto(teams = listOf(teamDto2, teamDto1))
    val expected = FoundFantaTournamentResults(tournament = tournament)

    assertThat(mySqlFantaTournamentsRepository.retrieveTournamentResults(1)).isEqualTo(expected)
  }

  private fun deleteAll() {

    fantaTournamentsDao.deleteAll()
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2222
  }
}