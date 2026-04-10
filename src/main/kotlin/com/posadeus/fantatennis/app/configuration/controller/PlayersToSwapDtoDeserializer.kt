package com.posadeus.fantatennis.app.configuration.controller

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.posadeus.fantatennis.controller.model.team.*

class PlayersToSwapDtoDeserializer : JsonDeserializer<PlayersToSwapDto>() {

  override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext): PlayersToSwapDto {

    val node = jsonParser.codec.readTree<JsonNode>(jsonParser)

    val remove =
        if (node.has("remove") && !node["remove"].isNull)
          jsonParser.codec.treeToValue(node["remove"], PlayersToRemoveDto::class.java)
        else
          PlayersToRemoveDto()

    val add =
        if (node.has("add") && !node["add"].isNull)
          jsonParser.codec.treeToValue(node["add"], PlayersToAddDto::class.java)
        else
          PlayersToAddDto()

    return PlayersToSwapDto(remove = remove, add = add)
  }
}
