package com.posadeus.fantatennis.app.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

    http {
      authorizeHttpRequests {
        // Public endpoints: landing page and the OAuth2 login/callback machinery.
        authorize("/", permitAll)
        authorize("/error", permitAll)
        authorize("/login/**", permitAll)
        authorize("/oauth2/**", permitAll)
        // Everything else requires an authenticated Google user.
        authorize(anyRequest, authenticated)
      }
      // CSRF is disabled because this service is consumed as a stateless REST API.
      // Re-enable it if you start serving state-changing forms from server-rendered pages.
      csrf { disable() }
      oauth2Login {
        // Where the user lands after a successful Google login.
        defaultSuccessUrl("/home", true)
      }
      logout {
        logoutSuccessUrl = "/"
      }
    }

    return http.build()
  }
}
