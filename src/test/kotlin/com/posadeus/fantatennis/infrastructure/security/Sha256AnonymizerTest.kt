package com.posadeus.fantatennis.infrastructure.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Sha256AnonymizerTest {

  @Test
  fun `is deterministic for the same input and pepper`() {

    val anonymizer = Sha256Anonymizer(A_PEPPER)

    assertThat(anonymizer.anonymize(AN_EMAIL)).isEqualTo(anonymizer.anonymize(AN_EMAIL))
  }

  @Test
  fun `produces a different output for a different pepper`() {

    val anonymized = Sha256Anonymizer(A_PEPPER).anonymize(AN_EMAIL)
    val anonymizedWithOtherPepper = Sha256Anonymizer(ANOTHER_PEPPER).anonymize(AN_EMAIL)

    assertThat(anonymized).isNotEqualTo(anonymizedWithOtherPepper)
  }

  @Test
  fun `produces a different output for a different input`() {

    val anonymizer = Sha256Anonymizer(A_PEPPER)

    assertThat(anonymizer.anonymize(AN_EMAIL)).isNotEqualTo(anonymizer.anonymize(ANOTHER_EMAIL))
  }

  @Test
  fun `produces a 64 char lowercase hex string`() {

    val anonymized = Sha256Anonymizer(A_PEPPER).anonymize(AN_EMAIL)

    assertThat(anonymized).matches("[0-9a-f]{64}")
  }

  companion object {

    private const val A_PEPPER = "a-secret-pepper"
    private const val ANOTHER_PEPPER = "another-secret-pepper"
    private const val AN_EMAIL = "mario.rossi@gmail.com"
    private const val ANOTHER_EMAIL = "luca.bianchi@gmail.com"
  }
}
