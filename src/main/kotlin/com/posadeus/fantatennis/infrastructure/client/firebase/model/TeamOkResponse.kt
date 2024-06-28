package com.posadeus.fantatennis.infrastructure.client.firebase.model

data class TeamOkResponse(val userId: Long,
                          val teamId: String,
                          val isTeamCompleted: Boolean,
                          val players: List<TeamPlayerResponse>?)
