package com.posadeus.fantatennis.infrastructure.repository.ausopen

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.ErrorTournamentInfo
import com.posadeus.fantatennis.domain.model.Round.R1
import com.posadeus.fantatennis.domain.model.Round.R2
import com.posadeus.fantatennis.infrastructure.client.ausopen.AusOpenClient
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenErrorResponse
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenMatchBuilder.Companion.anAusOpenMatch
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenOkResponseBuilder.Companion.anAusOpenOkResponse
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenPlayerBuilder.Companion.anAusOpenPlayer
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenRoundBuilder.Companion.anAusOpenRound
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenTeamBuilder.Companion.anAusOpenTeam
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenTeamDefinitionBuilder.Companion.anAusOpenTeamDefinition
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AusOpenTournamentInfoRepositoryTest {

  private val client: AusOpenClient = mockk()
  private val tournamentEventIdService: AusOpenTournamentEventIdService = mockk()

  private val repository: TournamentInfoRepository = AusOpenTournamentInfoRepository(client, tournamentEventIdService)

  @Test
  fun `can process`() {

    assertTrue { repository.canProcess(580) }
  }

  @Test
  fun `cannot process`() {

    assertFalse { repository.canProcess(NOT_580_TOURNAMENT_ID) }
  }

  @Test
  fun `get tournament information`() {

    val clientResponse = anAusOpenOkResponse()
        .withMatches(listOf(aMatchWith(ROUND_UUID_1, null, TEAM_UUID_2, "Winner", TEAM_UUID_1),
                            aMatchWith(ROUND_UUID_1, null, TEAM_UUID_4, "Winner", TEAM_UUID_3),
                            aMatchWith(ROUND_UUID_2, null, TEAM_UUID_3, "Winner", TEAM_UUID_1)))
        .withRounds(listOf(aRoundWith("1st Round", ROUND_UUID_1),
                           aRoundWith("2nd Round", ROUND_UUID_2)))
        .withTeams(listOf(aTeamDefinitionWith(PLAYER_UUID_1, TEAM_UUID_1),
                          aTeamDefinitionWith(PLAYER_UUID_2, TEAM_UUID_2),
                          aTeamDefinitionWith(PLAYER_UUID_3, TEAM_UUID_3),
                          aTeamDefinitionWith(PLAYER_UUID_4, TEAM_UUID_4)))
        .withPlayers(listOf(aPlayerWith("ATPPLAYER_TOUR_ID_1", PLAYER_UUID_1),
                            aPlayerWith("ATPPLAYER_TOUR_ID_2", PLAYER_UUID_2),
                            aPlayerWith("ATPPLAYER_TOUR_ID_3", PLAYER_UUID_3),
                            aPlayerWith("ATPPLAYER_TOUR_ID_4", PLAYER_UUID_4)))
        .build()
    val participants = setOf("PLAYER_TOUR_ID_1", "PLAYER_TOUR_ID_2", "PLAYER_TOUR_ID_3", "PLAYER_TOUR_ID_4")
    val expected = CompleteTournamentInfo(tournamentId = 580,
                                          participants = participants,
                                          winners = mapOf(R2 to setOf("PLAYER_TOUR_ID_1"),
                                                          R1 to setOf("PLAYER_TOUR_ID_1", "PLAYER_TOUR_ID_3")))

    every { tournamentEventIdService.retrieveEventId(A_YEAR) } returns AN_EVENT_NID
    every { client.retrieveDraws(AN_EVENT_NID) } returns clientResponse

    assertThat(repository.retrieveTournamentInfo(580, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `error from tournamentEventIdService`() {

    every { tournamentEventIdService.retrieveEventId(A_YEAR) } throws RuntimeException()

    assertThrows<RuntimeException> { repository.retrieveTournamentInfo(580, A_YEAR) }

    verify { client wasNot called }
  }

  @Test
  fun `tournament error from client`() {

    val expected = ErrorTournamentInfo

    every { tournamentEventIdService.retrieveEventId(A_YEAR) } returns AN_EVENT_NID
    every { client.retrieveDraws(AN_EVENT_NID) } returns AusOpenErrorResponse

    assertThat(repository.retrieveTournamentInfo(580, A_YEAR)).isEqualTo(expected)
  }

  private fun aMatchWith(roundUUID: String,
                         looserStatus: Nothing?,
                         looserTeamUUID: String,
                         winnerStatus: String,
                         winnerTeamUUID: String) =
      anAusOpenMatch()
          .withRoundId(roundUUID)
          .withTeams(listOf(aTeamWith(looserStatus, looserTeamUUID),
                            aTeamWith(winnerStatus, winnerTeamUUID)))
          .build()

  private fun aTeamWith(status: String?, teamUUID: String) =
      anAusOpenTeam()
          .withTeamId(teamUUID)
          .withStatus(status)
          .build()

  private fun aRoundWith(roundName: String, roundUUID: String) =
      anAusOpenRound()
          .withUuid(roundUUID)
          .withName(roundName)
          .build()

  private fun aTeamDefinitionWith(playerUUID: String, teamUUID: String) =
      anAusOpenTeamDefinition()
          .withUuid(teamUUID)
          .withPlayers(listOf(playerUUID))
          .build()

  private fun aPlayerWith(tourId: String, playerId: String) =
      anAusOpenPlayer()
          .withUuid(playerId)
          .withTourId(tourId)
          .build()

  companion object {

    private const val NOT_580_TOURNAMENT_ID = 123
    private const val AN_EVENT_NID = 245421
    private const val A_YEAR = 2024
    private const val ROUND_UUID_1 = "ROUND_UUID_1"
    private const val ROUND_UUID_2 = "ROUND_UUID_2"
    private const val TEAM_UUID_1 = "TEAM_UUID_1"
    private const val TEAM_UUID_2 = "TEAM_UUID_2"
    private const val TEAM_UUID_3 = "TEAM_UUID_3"
    private const val TEAM_UUID_4 = "TEAM_UUID_4"
    private const val PLAYER_UUID_1 = "PLAYER_UUID_1"
    private const val PLAYER_UUID_2 = "PLAYER_UUID_2"
    private const val PLAYER_UUID_3 = "PLAYER_UUID_3"
    private const val PLAYER_UUID_4 = "PLAYER_UUID_4"
  }
}