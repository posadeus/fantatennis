package com.posadeus.fantatennis.infrastructure.client.firebase

import com.posadeus.fantatennis.infrastructure.client.firebase.model.TeamResponse

interface FirebaseClient {

  fun retrieveTeam(userId: Long, teamId: String): TeamResponse {
    TODO("Not yet implemented")
  }
}
