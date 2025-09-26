package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

object DataBaseErrorManager {

  fun <T> manageError(errorResources: Collection<T>, batchUpdate: IntArray, func: (T) -> String): String {

    val errorIndexes = batchUpdate
        .withIndex()
        .filter { it.value == 0 }
        .map { it.index }

    return errorResources
        .filterIndexed { index, _ -> index in errorIndexes }
        .map(func)
        .reduce { acc, s -> "$acc, $s" }
  }
}