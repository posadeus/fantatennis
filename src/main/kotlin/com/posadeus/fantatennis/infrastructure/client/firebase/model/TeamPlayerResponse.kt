package com.posadeus.fantatennis.infrastructure.client.firebase.model

data class TeamPlayerResponse(val playerId: String,
                              val lastname: String,
                              val firstName: String,
                              val fantaPoints: Int,
                              val chosen: Boolean,
                              val playing: String?)
