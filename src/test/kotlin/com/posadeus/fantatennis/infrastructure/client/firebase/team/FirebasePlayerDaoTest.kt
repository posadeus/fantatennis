package com.posadeus.fantatennis.infrastructure.client.firebase.team

import com.google.firebase.database.*
import com.posadeus.fantatennis.infrastructure.client.firebase.model.FirebasePlayerResponse
import com.posadeus.fantatennis.infrastructure.client.firebase.model.FirebasePlayersResponse
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FirebasePlayerDaoTest {

  private val databaseReference: DatabaseReference = mockk()
  private val dataSnapshot: DataSnapshot = mockk()

  private val dao = FirebasePlayerDao(databaseReference)

  @Test
  fun `get all players`() {

    val expected = FirebasePlayersResponse(mapOf(A_PLAYER_ID to FirebasePlayerResponse(atpTourId = AN_ATP_TOUR_ID,
                                                                                       fantaPoints = A_FANTA_POINTS,
                                                                                       name = A_NAME)))

    every {
      databaseReference.addListenerForSingleValueEvent(any())
    } answers {
      firstArg<ValueEventListener>().onDataChange(dataSnapshot)
      mockk()
    }
    every { dataSnapshot.exists() } returns true
    every { dataSnapshot.getValue(FirebasePlayersResponse::class.java) } returns expected
    every { databaseReference.removeEventListener(any<ValueEventListener>()) } just runs

    runBlocking {
      assertThat(dao.getAllPlayers()).isEqualTo(expected)
    }
  }

  @Test
  fun `players not found`() {

    val expected = FirebasePlayersResponse(emptyMap())

    every {
      databaseReference.addListenerForSingleValueEvent(any())
    } answers {
      firstArg<ValueEventListener>().onDataChange(dataSnapshot)
      mockk()
    }
    every { dataSnapshot.exists() } returns false
    every { databaseReference.removeEventListener(any<ValueEventListener>()) } just runs

    runBlocking {
      assertThat(dao.getAllPlayers()).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val AN_ATP_TOUR_ID = "AN_ATP_TOUR_ID"
    private const val A_NAME = "A_NAME"
    private const val A_FANTA_POINTS = 20.9
  }
}