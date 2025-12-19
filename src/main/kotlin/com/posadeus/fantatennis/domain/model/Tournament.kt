package com.posadeus.fantatennis.domain.model

data class Tournament(val id: Int,
                      val tennisTvId: Int,
                      val name: String,
                      val points: Int,
                      val year: Int)