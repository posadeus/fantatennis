package com.posadeus.fantatennis.infrastructure.client.rolandgarros

import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosResponse

interface RolandGarrosClient {

  fun retrieveDraws(year: Int): RolandGarrosResponse
}
