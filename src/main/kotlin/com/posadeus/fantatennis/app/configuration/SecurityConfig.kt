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
        authorize("/", permitAll) // Grants the access to page "/"
        authorize("/login", permitAll) // Grants the access to page "/login"
        authorize("/login/oauth2/**", permitAll) // Needed for Google callback
//        authorize(anyRequest, authenticated) // FIXME remove the comment to require the Auth for every url not permitted
        authorize(anyRequest, permitAll) // FIXME Authorise everything: remove when login is in place
      }
      oauth2Login {
        defaultSuccessUrl("/home", true) // Redirect to "/home" after Google login
      }
    }

    return http.build()
  }
}
