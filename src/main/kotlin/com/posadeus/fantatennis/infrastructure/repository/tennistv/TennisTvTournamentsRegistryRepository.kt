package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRegistryRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.NotFoundTournamentsRegistry
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentRegistry
import org.slf4j.LoggerFactory

class TennisTvTournamentsRegistryRepository(private val tennisTvClient: TennisTvClient) : TournamentsRegistryRepository {

  override fun retrieveAllTournamentsFor(year: Int): TournamentsRegistry =
      year
          .let(tennisTvClient::retrieveTournamentsRegistry)
          .tournaments
          .takeIf { it.isNotEmpty() }
          ?.filter(::isAcceptableTournament)
          ?.map(::toTournamentRegistry)
          ?.let(::FoundTournamentsRegistry)
      ?: NotFoundTournamentsRegistry

  private fun isAcceptableTournament(it: TennisTvTournamentRegistry) =
      it.type in ACCEPTED_TOURNAMENT_TYPES

  private fun toTournamentRegistry(it: TennisTvTournamentRegistry): TournamentRegistry =
      TournamentRegistry(atpTourId = it.id,
                         tennisTvId = it.id,
                         name = it.name,
                         startDate = it.start,
                         endDate = it.end,
                         year = it.year,
                         points = toPoints(it.type),
                         surface = toSurface(it.surface),
                         location = it.location)

  private fun toSurface(surface: String): Surface =
      try {

        Surface.valueOf(surface)
      }
      catch (e: Exception) {

        LOGGER.error("Unexpected Surface: $surface")
        throw e
      }

  private fun toPoints(type: String): Int =
      when (type) {

        "GS" -> 2000
        else -> type.toInt()
      }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(TennisTvTournamentsRegistryRepository::class.java)

    private val ACCEPTED_TOURNAMENT_TYPES = setOf("250", "500", "1000", "GS")
  }
}
