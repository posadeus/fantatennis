package com.posadeus.fantatennis.infrastructure.client.firebase.team

import com.google.firebase.database.*
import com.posadeus.fantatennis.infrastructure.client.firebase.model.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseTeamDao(private val databaseReference: DatabaseReference) {

  suspend fun getTeam(teamId: String): FirebaseTeamResponse =
      suspendCancellableCoroutine { continuation ->

        val reference = databaseReference.child(teamId)
        val listener = object : ValueEventListener {

          override fun onDataChange(dataSnapshot: DataSnapshot) {

            if (dataSnapshot.exists()) {

              continuation.resume(dataSnapshot.getValue(FoundFirebaseTeamResponse::class.java))
            }
            else {

              continuation.resume(NotFoundFirebaseTeamResponse)
            }
          }

          override fun onCancelled(databaseError: DatabaseError) {

            continuation.resumeWithException(databaseError.toException())
          }
        }

        reference.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            reference.removeEventListener(listener)
        }
      }
}
