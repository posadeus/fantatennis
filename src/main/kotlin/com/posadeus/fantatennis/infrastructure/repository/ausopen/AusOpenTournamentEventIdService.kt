package com.posadeus.fantatennis.infrastructure.repository.ausopen

class AusOpenTournamentEventIdService {

  fun retrieveEventId(year: Int): Int =
      when (year) {
        2024 -> EVENT_NID_2024
        2025 -> EVENT_NID_2025
        2026 -> EVENT_NID_2026
        else -> throw RuntimeException("No Australian Open EVENT_NID added for $year")
      }

  companion object {

    private const val EVENT_NID_2024 = 245421
    private const val EVENT_NID_2025 = 251300
    private const val EVENT_NID_2026 = 257391
  }
}
