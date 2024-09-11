package com.posadeus.fantatennis.infrastructure.client.wimbledon

import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.WimbledonResponse

interface WimbledonClient {

  fun retrieveDraws(year: Int): WimbledonResponse
}
