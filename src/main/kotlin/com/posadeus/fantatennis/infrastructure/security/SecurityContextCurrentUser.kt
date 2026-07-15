package com.posadeus.fantatennis.infrastructure.security

import com.posadeus.fantatennis.domain.infrastructure.CurrentUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * Reads the authenticated user's email from the Spring Security context.
 *
 * Google logins come back as [OidcUser] (openid scope requested);
 * the plain [OAuth2User] branch is a defensive fallback for non-OIDC providers.
 */
class SecurityContextCurrentUser : CurrentUser {

  override fun email(): String =
      when (val principal = SecurityContextHolder.getContext().authentication?.principal) {
        is OidcUser -> principal.email
        is OAuth2User -> principal.getAttribute<String>("email")
        else -> null
      }
      ?: throw IllegalStateException("No authenticated user with an email in the security context")
}
