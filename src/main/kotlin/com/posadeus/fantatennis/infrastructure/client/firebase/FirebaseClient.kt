package com.posadeus.fantatennis.infrastructure.client.firebase

import com.posadeus.fantatennis.infrastructure.client.firebase.model.TeamOkResponse

interface FirebaseClient {

  fun retrieveTeam(userId: Long, teamId: String): TeamOkResponse {
    TODO("Not yet implemented")
  }
}
