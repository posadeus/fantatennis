package com.posadeus.fantatennis.infrastructure.client.firebase.team

import com.google.firebase.database.*
import com.posadeus.fantatennis.infrastructure.client.firebase.model.FirebasePlayersResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebasePlayerDao(private val databaseReference: DatabaseReference) {

  suspend fun getAllPlayers(): FirebasePlayersResponse =
      suspendCancellableCoroutine { continuation ->

        val listener = object : ValueEventListener {

          override fun onDataChange(dataSnapshot: DataSnapshot) {

            if (dataSnapshot.exists()) {

              continuation.resume(dataSnapshot.getValue(FirebasePlayersResponse::class.java))
            }
            else {

              continuation.resume(FirebasePlayersResponse(emptyMap()))
            }
          }

          override fun onCancelled(databaseError: DatabaseError) {

            continuation.resumeWithException(databaseError.toException())
          }
        }

        databaseReference.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            databaseReference.removeEventListener(listener)
        }
      }
}
