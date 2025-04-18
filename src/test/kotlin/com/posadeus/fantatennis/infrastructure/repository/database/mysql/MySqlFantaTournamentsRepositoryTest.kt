package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Optional.empty
import java.util.Optional.of

class MySqlFantaTournamentsRepositoryTest {

  private val fantaTournamentsDao: FantaTournamentsDao = mockk()

  private val repository: FantaTournamentsRepository = MySqlFantaTournamentsRepository(fantaTournamentsDao)

  @Nested
  inner class RetrieveFantaTournament {

    @Test
    fun `retrieve found a result`() {

      val fantaTournamentsEntity = FantaTournamentsEntity(id = A_TOURNAMENT_ID,
                                                          startingTournament = A_STARTING_TOURNAMENT_ID,
                                                          endingTournament = AN_ENDING_TOURNAMENT_ID,
                                                          year = A_TOURNAMENT_YEAR)

      val expected = ValidFantaTournament(id = A_TOURNAMENT_ID,
                                          startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                          tournamentYear = A_TOURNAMENT_YEAR)

      every { fantaTournamentsDao.findById(A_TOURNAMENT_ID) } returns of(fantaTournamentsEntity)

      assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `retrieve found no results`() {

      val expected = InvalidFantaTournament

      every { fantaTournamentsDao.findById(A_TOURNAMENT_ID) } returns empty()

      assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `error during retrieve operation`() {

      val expected = InvalidFantaTournament

      every { fantaTournamentsDao.findById(A_TOURNAMENT_ID) } throws Exception()

      assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 10
    private const val A_TOURNAMENT_YEAR = 2022
  }
}