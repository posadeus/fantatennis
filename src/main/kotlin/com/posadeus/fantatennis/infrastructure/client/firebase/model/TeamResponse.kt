package com.posadeus.fantatennis.infrastructure.client.firebase.model

sealed interface TeamResponse

data class TeamOkResponse(val userId: Long,
                          val teamId: String,
                          val isTeamCompleted: Boolean,
                          val players: List<TeamPlayerResponse>?) : TeamResponse

data object TeamNotFoundResponse : TeamResponse