package com.posadeus.fantatennis.infrastructure.client.firebase.team

import com.google.firebase.database.*
import com.posadeus.fantatennis.infrastructure.client.firebase.model.FoundFirebaseTeamResponse
import com.posadeus.fantatennis.infrastructure.client.firebase.model.NotFoundFirebaseTeamResponse
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FirebaseTeamDaoTest {

  private val databaseReference: DatabaseReference = mockk()
  private val dataSnapshot: DataSnapshot = mockk()

  private val dao = FirebaseTeamDao(databaseReference)

  @Test
  fun `get team`() {

    val expected = FoundFirebaseTeamResponse(id = A_TEAM_ID,
                                             players = listOf())

    every {
      databaseReference.child(A_TEAM_ID).addListenerForSingleValueEvent(any())
    } answers {
      firstArg<ValueEventListener>().onDataChange(dataSnapshot)
      mockk()
    }
    every { dataSnapshot.exists() } returns true
    every { dataSnapshot.getValue(FoundFirebaseTeamResponse::class.java) } returns expected
    every { databaseReference.removeEventListener(any<ValueEventListener>()) } just runs

    runBlocking {
      assertThat(dao.getTeam(A_TEAM_ID)).isEqualTo(expected)
    }
  }

  @Test
  fun `team not found`() {

    val expected = NotFoundFirebaseTeamResponse

    every {
      databaseReference.child(A_TEAM_ID).addListenerForSingleValueEvent(any())
    } answers {
      firstArg<ValueEventListener>().onDataChange(dataSnapshot)
      mockk()
    }
    every { dataSnapshot.exists() } returns false
    every { databaseReference.removeEventListener(any<ValueEventListener>()) } just runs

    runBlocking {
      assertThat(dao.getTeam(A_TEAM_ID)).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_TEAM_ID = "A_TEAM_ID"
  }
}