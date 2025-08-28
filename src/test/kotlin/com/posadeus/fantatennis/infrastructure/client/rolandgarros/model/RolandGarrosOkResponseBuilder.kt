package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosTournamentEventBuilder.Companion.aRolandGarrosTournamentEvent

class RolandGarrosOkResponseBuilder(private var title: Any? = null,
                                    private var description: Any? = null,
                                    private var downloadPdfLabel: Any? = null,
                                    private var fullDrawDownloadPdfLabel: Any? = null,
                                    private var noResultsLabel: Any? = null,
                                    private var searchPlayerPlaceholder: Any? = null,
                                    private var favoriteLabel: Any? = null,
                                    private var types: Any? = null,
                                    private var eventYears: Any? = null,
                                    private var currentRound: Any? = null,
                                    private var tournamentEvent: RolandGarrosTournamentEvent = aRolandGarrosTournamentEvent().build(),
                                    private var isRgBracketEnabled: Any? = null,
                                    private var bracketButtonLabel: Any? = null,
                                    private var rgBracketUrl: Any? = null,
                                    private var pdfFileName: Any? = null,
                                    private var orLabel: Any? = null) {

  fun withTournamentEvent(tournamentEvent: RolandGarrosTournamentEvent): RolandGarrosOkResponseBuilder {
    this.tournamentEvent = tournamentEvent
    return this
  }

  fun build() = RolandGarrosOkResponse(title,
                                       description,
                                       downloadPdfLabel,
                                       fullDrawDownloadPdfLabel,
                                       noResultsLabel,
                                       searchPlayerPlaceholder,
                                       favoriteLabel,
                                       types,
                                       eventYears,
                                       currentRound,
                                       tournamentEvent,
                                       isRgBracketEnabled,
                                       bracketButtonLabel,
                                       rgBracketUrl,
                                       pdfFileName,
                                       orLabel)

  companion object {

    fun aRolandGarrosOkResponse() =
        RolandGarrosOkResponseBuilder()
  }
}