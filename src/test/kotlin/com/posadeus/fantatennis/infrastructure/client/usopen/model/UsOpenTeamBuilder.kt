package com.posadeus.fantatennis.infrastructure.client.usopen.model

class UsOpenTeamBuilder(private var firstNameA: Any? = null,
                        private var lastNameA: Any? = null,
                        private var displayNameA: Any? = null,
                        private var idA: String = "atpAN_ID",
                        private var nationA: Any? = null,
                        private var firstNameB: Any? = null,
                        private var lastNameB: Any? = null,
                        private var displayNameB: Any? = null,
                        private var idB: Any? = null,
                        private var nationB: Any? = null,
                        private var seed: Any? = null,
                        private var entryStatus: Any? = null,
                        private var totalSetsWon: Any? = null,
                        private var won: Boolean? = false,
                        private var serve: Any? = null) {

  fun withIdA(idA: String): UsOpenTeamBuilder {
    this.idA = idA
    return this
  }

  fun withWon(won: Boolean?): UsOpenTeamBuilder {
    this.won = won
    return this
  }

  fun build() = UsOpenTeam(firstNameA,
                           lastNameA,
                           displayNameA,
                           idA,
                           nationA,
                           firstNameB,
                           lastNameB,
                           displayNameB,
                           idB,
                           nationB,
                           seed,
                           entryStatus,
                           totalSetsWon,
                           won,
                           serve)

  companion object {

    fun aUsOpenTeam() =
        UsOpenTeamBuilder()
  }
}