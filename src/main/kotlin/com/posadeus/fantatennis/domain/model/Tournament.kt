package com.posadeus.fantatennis.domain.model

// FIXME Remove nullability after the changes to points calculation
data class Tournament(val id: Int,
                      val tennisTvId: Int,
                      val points: Int,
                      val year: Int? = null)