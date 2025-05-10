package com.posadeus.fantatennis.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EntityScan(basePackages = ["com.posadeus.fantatennis.infrastructure.repository.database.mysql.model"])
@EnableJpaRepositories(basePackages = ["com.posadeus.fantatennis.infrastructure.repository.database"])
class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}