package com.posadeus.fantatennis.infrastructure.client.usopen

import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenResponse

interface UsOpenClient {

  fun retrieveDraws(year: Int): UsOpenResponse
}
