package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*

class MySqlPlayerPointsRepository(private val playersPointsDao: PlayersPointsDao) : PlayerPointsRepository {

  override fun save(players: Set<DomainPlayer>) {

    playersPointsDao.saveAll(toPlayersPointsEntity(players))
  }

  override fun retrieve(tournamentByTeam: TournamentByTeam): TeamOrderedPlayerPoints =
      playersPointsDao.findPlayersPointsByTeamTournamentDto(tournamentByTeam.let(::toTeamTournamentDto))
          .let(::toPlayerPoints)
          .let(::TeamOrderedPlayerPoints)

  private fun toPlayerPoints(teamPlayerPoints: List<TeamPlayerPointsDto>): List<PlayerPoints> =
      teamPlayerPoints
          .map {
            PlayerPoints(playerId = it.playerId,
                         playerName = it.playerName,
                         totalPoints = it.totalScore)
          }

  private fun toTeamTournamentDto(tournamentByTeam: TournamentByTeam): TeamTournamentDto =
      tournamentByTeam
          .let {
            TeamTournamentDto(teamId = it.teamId,
                              startingTournamentId = it.startingTournamentId,
                              endingTournamentId = it.endingTournamentId,
                              tournamentYear = it.tournamentYear)
          }

  private fun toPlayersPointsEntity(players: Set<DomainPlayer>): Set<PlayersPointsEntity> =
      players.flatMap { player ->
        val playerId = player.id
        player.tournamentPoints.flatMap { tournament ->
          val year = tournament.key
          tournament.value.map {
            PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = year,
                                                              tournamentId = it.key,
                                                              playerId = playerId),
                                fantaPoints = it.value,
                                player = PlayersEntity(id = playerId),
                                tournament = TournamentsEntity(id = it.key))
          }
        }
      }.toSet()
}
