package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentResultsDto
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
import org.slf4j.LoggerFactory

class MySqlFantaTournamentsRepository(private val fantaTournamentsDao: FantaTournamentsDao) : FantaTournamentsRepository {

  override fun create(tournamentToCreate: TournamentToCreateDto): FantaTournament =
      try {

        tournamentToCreate
            .let(::toFantaTournamentsEntityToCreate)
            .let(fantaTournamentsDao::save)
            .let(::toValidFantaTournament)
      }
      catch (e: Exception) {

        LOGGER.error("Error during save operation of the new fanta tournament", e)
        InvalidFantaTournament
      }

  override fun retrieve(tournamentId: Int): FantaTournament =
      try {
        tournamentId
            .let(fantaTournamentsDao::findById)
            .map(::toValidFantaTournament)
            .orElse(InvalidFantaTournament)
      }
      catch (e: Exception) {

        LOGGER.error("Error during retrieve operation for tournament id: $tournamentId")
        InvalidFantaTournament
      }

  override fun retrieveTournamentResults(tournamentId: Int): FantaTournamentResults =
      fantaTournamentsDao.findTournamentResultsByTournamentId(tournamentId)
          .let(::toTournamentDto)
          .let(::FoundFantaTournamentResults)

  private fun toTournamentDto(resultsDto: List<TournamentResultsDto>): TournamentDto =
      resultsDto
          .groupBy { it.getTeamId() }
          .map(::toTeamDto)
          .sortedByDescending { it.totalScore }
          .let(::TournamentDto)

  private fun toTeamDto(entries: Map.Entry<Int, List<TournamentResultsDto>>) =
      TeamDto(players = entries.value.map(::toTeamPlayerDto),
              totalScore = calculateTeamTotalScore(entries.value))

  private fun calculateTeamTotalScore(dtos: List<TournamentResultsDto>) =
      dtos.map { it.getPlayerTotalScore() }
          .reduce { teamTotalScore, singlePlayerScore -> teamTotalScore + singlePlayerScore }

  private fun toTeamPlayerDto(dto: TournamentResultsDto) =
      TeamPlayerDto(fullName = dto.getPlayerFullName(),
                    fantaPoints = dto.getPlayerTotalScore())

  private fun toFantaTournamentsEntityToCreate(dto: TournamentToCreateDto): FantaTournamentsEntity =
      FantaTournamentsEntity(startingTournament = dto.startingTournamentId,
                             endingTournament = dto.endingTournamentId,
                             year = dto.tournamentYear)

  private fun toValidFantaTournament(entity: FantaTournamentsEntity): FantaTournament =
      ValidFantaTournament(id = entity.id,
                           startingTournamentId = entity.startingTournament,
                           endingTournamentId = entity.endingTournament,
                           tournamentYear = entity.year)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(MySqlFantaTournamentsRepository::class.java)
  }
}

