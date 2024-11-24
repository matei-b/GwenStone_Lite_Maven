package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Game {
    private int totalGamesPlayed = 0;
    private int playerOneWins = 0;
    private int playerTwoWins = 0;

    /**
     * Prints the total number of games played.
     *
     * @param mapper The Jackson ObjectMapper used to create JSON objects.
     * @param output The ArrayNode to which the JSON object will be added.
     */
    public void printTotalGamesPlayed(final ObjectMapper mapper,
                                      final ArrayNode output) {
        ObjectNode gamesNode = mapper.createObjectNode();

        gamesNode.put("command", "getTotalGamesPlayed");
        gamesNode.put("output", totalGamesPlayed);

        output.add(gamesNode);
    }

    /**
     * Prints the number of wins by player one.
     *
     * @param mapper The Jackson ObjectMapper used to create JSON objects.
     * @param output The ArrayNode to which the JSON object will be added.
     */
    public void printPlayerOneWins(final ObjectMapper mapper,
                                   final ArrayNode output) {
        ObjectNode gamesNode = mapper.createObjectNode();

        gamesNode.put("command", "getPlayerOneWins");
        gamesNode.put("output", playerOneWins);

        output.add(gamesNode);
    }

    /***
     * Getter
     */
    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    /***
     * Getter
     */
    public int getPlayerOneWins() {
        return playerOneWins;
    }

    /***
     * Getter
     */
    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    /***
     * Setter
     */
    public void setTotalGamesPlayed(final int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    /***
     * Setter
     */
    public void setPlayerOneWins(final int playerOneWins) {
        this.playerOneWins = playerOneWins;
    }

    /***
     * Setter
     */
    public void setPlayerTwoWins(final int playerTwoWins) {
        this.playerTwoWins = playerTwoWins;
    }

    /**
     * Prints the number of wins by player two.
     *
     * @param mapper The Jackson ObjectMapper used to create JSON objects.
     * @param output The ArrayNode to which the JSON object will be added.
     */
    public void printPlayerTwoWins(final ObjectMapper mapper,
                                   final ArrayNode output) {
        ObjectNode gamesNode = mapper.createObjectNode();

        gamesNode.put("command", "getPlayerTwoWins");
        gamesNode.put("output", playerTwoWins);

        output.add(gamesNode);
    }
}
