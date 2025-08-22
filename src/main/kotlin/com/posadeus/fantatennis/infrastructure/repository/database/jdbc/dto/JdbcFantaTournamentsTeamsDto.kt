package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

data class JdbcFantaTournamentsTeamsDto(val teamId: Int,
                                        val startingTournamentId: Int,
                                        val endingTournamentId: Int,
                                        val tournamentYear: Int,
                                        val ownerId: String = "") // FIXME: remove default value