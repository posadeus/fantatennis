package com.posadeus.fantatennis.domain.service.fantatournament

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto
import com.posadeus.fantatennis.domain.model.FantaTournamentResults
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class RetrieveFantaTournamentMigrationIT {

    private val gson = Gson()

    @ParameterizedTest
    @MethodSource("tournamentIds")
    fun `old and new implementations produce identical results`(tournamentId: String) {
        val oldJson = loadFixture("migration/old-$tournamentId.json")
        val newJson = loadFixture("migration/new-$tournamentId.json")

        val oldResult = deserialize(oldJson)
        val newResult = deserialize(newJson)

        assertThat(newResult).isEqualTo(oldResult)
    }

    private fun loadFixture(path: String): String =
        javaClass.classLoader.getResourceAsStream(path)
            ?.bufferedReader()
            ?.readText()
            ?: error("Fixture not found: $path")

    private fun deserialize(json: String): FantaTournamentResults {
        val envelope = gson.fromJson(json, Envelope::class.java)
        return when (envelope.type) {
            "FoundFantaTournamentResults" ->
                FoundFantaTournamentResults(gson.fromJson(envelope.payload, FantaTournamentDto::class.java))
            "NotFoundFantaTournamentId" -> NotFoundFantaTournamentId
            "ErrorFantaTournamentResults" -> ErrorFantaTournamentResults
            else -> error("Unknown type: ${envelope.type}")
        }
    }

    private data class Envelope(val type: String, val payload: JsonElement)

    companion object {
        // Add tournament IDs here. Drop old-{id}.json and new-{id}.json in src/test/resources/migration/
        @JvmStatic
        fun tournamentIds() = listOf(
            "PLACEHOLDER_TOURNAMENT_ID"
        )
    }
}
