package com.posadeus.fantatennis.infrastructure.client.firebase

import com.posadeus.fantatennis.infrastructure.client.firebase.model.TeamResponse

interface FirebaseClient {

  fun retrieveTeam(userId: String, teamId: String): TeamResponse {
    TODO("Not yet implemented")
  }
}
