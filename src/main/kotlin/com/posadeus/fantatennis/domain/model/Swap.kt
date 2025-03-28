package com.posadeus.fantatennis.domain.model

sealed interface Swap {

  data object SwapCompleted : Swap
  data object SwapFailed : Swap
}