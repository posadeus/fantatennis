package com.posadeus.fantatennis.infrastructure.client.tennistv.model

data class TennisTvTournamentsRegistryResponse(val tournaments: List<TennisTvTournamentRegistry>)

data class TennisTvTournamentRegistry(val id: Int,
                                      val year: Int,
                                      val name: String,
                                      val start: String,
                                      val end: String,
                                      val type: String,
                                      val location: String,
                                      val surface: String)