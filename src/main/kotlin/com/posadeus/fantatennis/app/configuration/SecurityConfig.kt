package com.posadeus.fantatennis.app.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

  @Bean
  fun securityFilterChain(http: HttpSecurity,
                          adminAwareOidcUserService: OAuth2UserService<OidcUserRequest, OidcUser>): SecurityFilterChain {

    http {
      authorizeHttpRequests {
        // Public endpoints: landing page and the OAuth2 login/callback machinery.
        authorize("/", permitAll)
        authorize("/error", permitAll)
        authorize("/login/**", permitAll)
        authorize("/oauth2/**", permitAll)
        // Data-import jobs are restricted to the configured admin account.
        authorize("/job/**", hasRole("ADMIN"))
        // Everything else requires an authenticated Google user.
        authorize(anyRequest, authenticated)
      }
      // CSRF is disabled because this service is consumed as a stateless REST API.
      // Re-enable it if you start serving state-changing forms from server-rendered pages.
      csrf { disable() }
      oauth2Login {
        userInfoEndpoint {
          oidcUserService = adminAwareOidcUserService
        }
        // Where the user lands after a successful Google login.
        defaultSuccessUrl("/home", true)
      }
      logout {
        logoutSuccessUrl = "/"
      }
    }

    return http.build()
  }

  /**
   * Enriches the Google user with application roles.
   *
   * Every authenticated user gets ROLE_USER; the single address configured in
   * `app.security.admin-email` additionally gets ROLE_ADMIN. This is intentionally a
   * hard-coded, email-based rule for now, see OAuth-Evolution.md for the planned move to
   * a User/Account entity with DB-backed roles.
   */
  @Bean
  fun adminAwareOidcUserService(@Value("\${app.security.admin-email}") adminEmail: String)
      : OAuth2UserService<OidcUserRequest, OidcUser> {

    val delegate = OidcUserService()

    return OAuth2UserService { request ->

      val user = delegate.loadUser(request)

      val authorities = user.authorities.toMutableSet()
      authorities.add(SimpleGrantedAuthority("ROLE_USER"))

      if (user.email.equals(adminEmail, ignoreCase = true)) {

        authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
      }

      DefaultOidcUser(authorities, user.idToken, user.userInfo)
    }
  }
}
