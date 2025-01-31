package com.posadeus.fantatennis.controller.login

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

  @GetMapping("/home")
  fun home(authentication: Authentication): String {

    when (val user = authentication.principal) {

      is OidcUser -> {

        println("Email: ${user.email}")
        println("User Info: ${user.attributes}")
      }

      is OAuth2User -> {

        println("Email: ${user.attributes["email"]}")
        println("User Info: ${user.attributes}")
      }

      else -> println("Tipo di utente non supportato")
    }

    return "/tournament/3" // Redirect to url
  }
}