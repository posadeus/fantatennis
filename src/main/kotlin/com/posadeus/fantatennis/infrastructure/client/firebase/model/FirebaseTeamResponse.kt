package com.posadeus.fantatennis.infrastructure.client.firebase.model

sealed interface FirebaseTeamResponse

data class FoundFirebaseTeamResponse(val id: String,
                                     val players: List<FirebaseTeamPlayerResponse>) : FirebaseTeamResponse

data object NotFoundFirebaseTeamResponse : FirebaseTeamResponse