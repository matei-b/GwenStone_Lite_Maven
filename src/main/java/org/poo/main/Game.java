package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Game {
    int totalGamesPlayed = 0;
    int playerOneWins = 0;
    int playerTwoWins = 0;

    public void printTotalGamesPlayed(ObjectMapper mapper, ArrayNode output) {
        ObjectNode gamesNode = mapper.createObjectNode();

        gamesNode.put("command", "getTotalGamesPlayed");
        gamesNode.put("output", totalGamesPlayed);

        output.add(gamesNode);
    }

    public void printPlayerOneWins(ObjectMapper mapper, ArrayNode output) {
        ObjectNode gamesNode = mapper.createObjectNode();

        gamesNode.put("command", "getPlayerOneWins");
        gamesNode.put("output", playerOneWins);

        output.add(gamesNode);
    }

    public void printPlayerTwoWins(ObjectMapper mapper, ArrayNode output) {
        ObjectNode gamesNode = mapper.createObjectNode();

        gamesNode.put("command", "getPlayerTwoWins");
        gamesNode.put("output", playerTwoWins);

        output.add(gamesNode);
    }
}
