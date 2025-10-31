package com.posadeus.fantatennis.infrastructure

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.assertThrows

inline fun <reified T : Throwable> assertThrowsWithMessage(expectedMessage: String, block: () -> Unit) {

  val exception = assertThrows<T> { block() }

  assertThat(exception.message).isEqualTo(expectedMessage)
}