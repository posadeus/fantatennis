package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

class RolandGarrosMatchDataBuilder(private var type: Any? = null,
                                   private var typeLabel: Any? = null,
                                   private var round: Any? = null,
                                   private var roundLabel: Any? = null,
                                   private var courtName: Any? = null,
                                   private var durationInMinutes: Any? = null,
                                   private var endTimestamp: Any? = null,
                                   private var startingAt: Any? = null,
                                   private var dateSchedule: Any? = null,
                                   private var notBefore: Any? = null,
                                   private var status: Any? = null,
                                   private var statusLabel: String? = null,
                                   private var notBeforeLabel: Any? = null,
                                   private var fromLabel: Any? = null,
                                   private var isNightSession: Any? = null,
                                   private var nightSessionLabel: Any? = null,
                                   private var daySessionLabel: Any? = null) {

  fun withStatusLabel(statusLabel: String?): RolandGarrosMatchDataBuilder {
    this.statusLabel = statusLabel
    return this
  }

  fun build() = RolandGarrosMatchData(type,
                                      typeLabel,
                                      round,
                                      roundLabel,
                                      courtName,
                                      durationInMinutes,
                                      endTimestamp,
                                      startingAt,
                                      dateSchedule,
                                      notBefore,
                                      status,
                                      statusLabel,
                                      notBeforeLabel,
                                      fromLabel,
                                      isNightSession,
                                      nightSessionLabel,
                                      daySessionLabel)

  companion object {

    fun aRolandGarrosMatchData() =
        RolandGarrosMatchDataBuilder()
  }
}