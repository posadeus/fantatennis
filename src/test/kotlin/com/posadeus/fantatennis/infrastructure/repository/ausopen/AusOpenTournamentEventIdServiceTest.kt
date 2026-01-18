package com.posadeus.fantatennis.infrastructure.repository.ausopen

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AusOpenTournamentEventIdServiceTest {

  private val service = AusOpenTournamentEventIdService()

  @Test
  fun `exception thrown`() {

    assertThrows<RuntimeException> { service.retrieveEventId(2023) }
  }

  @Test
  fun `year 2024`() {

    assertThat(service.retrieveEventId(2024)).isEqualTo(245421)
  }

  @Test
  fun `year 2025`() {

    assertThat(service.retrieveEventId(2025)).isEqualTo(251300)
  }

  @Test
  fun `year 2026`() {

    assertThat(service.retrieveEventId(2026)).isEqualTo(257391)
  }
}