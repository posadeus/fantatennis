package com.posadeus.fantatennis.infrastructure.security

import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser

class SecurityContextCurrentUserTest {

  private val anonymizer: Anonymizer = mockk()

  private val currentUser = SecurityContextCurrentUser(anonymizer)

  @BeforeEach
  fun setUp() {
    mockkStatic(SecurityContextHolder::class)
  }

  @AfterEach
  fun tearDown() {
    unmockkStatic(SecurityContextHolder::class)
  }

  @Test
  fun `anonymizes the normalized email of the OIDC user`() {

    givenPrincipalEmail("  Mario.Rossi@Gmail.com  ")
    val normalizedEmail = "mario.rossi@gmail.com"

    every { anonymizer.anonymize(normalizedEmail) } returns AN_ANONYMIZED_ID

    assertThat(currentUser.ownerId()).isEqualTo(AN_ANONYMIZED_ID)

    verify(exactly = 1) { anonymizer.anonymize(normalizedEmail) }
  }

  @Test
  fun `throws when there is no authenticated user`() {

    every { SecurityContextHolder.getContext() } returns mockk {
      every { authentication } returns null
    }

    assertThatThrownBy { currentUser.ownerId() }
        .isInstanceOf(IllegalStateException::class.java)
  }

  private fun givenPrincipalEmail(email: String) {

    val principal = mockk<OidcUser> {
      every { this@mockk.email } returns email
    }
    val authentication = mockk<Authentication> {
      every { this@mockk.principal } returns principal
    }
    val context = mockk<SecurityContext> {
      every { this@mockk.authentication } returns authentication
    }
    every { SecurityContextHolder.getContext() } returns context
  }

  companion object {

    private const val AN_ANONYMIZED_ID = "an-anonymized-id"
  }
}
