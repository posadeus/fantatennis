package com.posadeus.fantatennis.infrastructure.client.ausopen

import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenResponse

interface AusOpenClient {

  fun retrieveDraws(eventNid: Int): AusOpenResponse
}
