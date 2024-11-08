package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTeamsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FantaTeamsDao : JpaRepository<FantaTeamsEntity, Int>
