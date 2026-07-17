package com.posadeus.fantatennis.infrastructure.security

interface Anonymizer {

  fun anonymize(value: String): String
}
