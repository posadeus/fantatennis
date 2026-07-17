package com.posadeus.fantatennis.infrastructure.security

import java.security.MessageDigest

class Sha256Anonymizer(private val pepper: String) : Anonymizer {

  override fun anonymize(value: String): String =
      MessageDigest.getInstance("SHA-256")
          .digest((value + pepper).toByteArray(Charsets.UTF_8))
          .joinToString("") { "%02x".format(it) }
}
