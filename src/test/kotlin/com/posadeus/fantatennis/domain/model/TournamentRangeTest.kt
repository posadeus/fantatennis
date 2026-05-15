package com.posadeus.fantatennis.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class TournamentRangeTest {

  @ParameterizedTest
  @ValueSource(ints = [2, 3, 4, 5])
  fun `contains returns true for tournament within bounded range`(tournamentId: Int) {

    val range = TournamentRange(start = 2, end = 5)

    assertThat(range.contains(tournamentId)).isTrue
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 2])
  fun `contains returns false for tournament before range start`(tournamentId: Int) {

    val range = TournamentRange(start = 3, end = 5)

    assertThat(range.contains(tournamentId)).isFalse
  }

  @ParameterizedTest
  @ValueSource(ints = [5, 6])
  fun `contains returns false for tournament after range end`(tournamentId: Int) {

    val range = TournamentRange(start = 2, end = 4)

    assertThat(range.contains(tournamentId)).isFalse
  }

  @ParameterizedTest
  @ValueSource(ints = [3, 4, 100])
  fun `contains returns true for tournament within open-ended range`(tournamentId: Int) {

    val range = TournamentRange(start = 3, end = null)

    assertThat(range.contains(tournamentId)).isTrue
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 2])
  fun `contains returns false for tournament before open-ended range start`(tournamentId: Int) {

    val range = TournamentRange(start = 3, end = null)

    assertThat(range.contains(tournamentId)).isFalse
  }

  @ParameterizedTest
  @ValueSource(ints = [5])
  fun `contains returns true for single tournament range`(tournamentId: Int) {

    val range = TournamentRange(start = 5, end = 5)

    assertThat(range.contains(tournamentId)).isTrue
  }

  @ParameterizedTest
  @ValueSource(ints = [4, 6])
  fun `contains returns false for single tournament range boundaries`(tournamentId: Int) {

    val range = TournamentRange(start = 5, end = 5)

    assertThat(range.contains(tournamentId)).isFalse
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 6])
  fun `contains returns false for tournament at boundary outside range`(tournamentId: Int) {

    val range = TournamentRange(start = 2, end = 5)

    assertThat(range.contains(tournamentId)).isFalse
  }
}
