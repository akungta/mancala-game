package com.akashrungta.game.mancala.core;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@JsonSnakeCase
@Data
@AllArgsConstructor
public class BoardView {

  String sessionId;

  GameState gameState;

  Player nextPlayer;

  Map<Player, PlayerPits> playerPits;

  @JsonSnakeCase
  @Data
  @AllArgsConstructor
  public static class PlayerPits {

    Map<Integer, Integer> pits;

    int bigPit;
  }
}
