package com.akashrungta.game.mancala.core;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Value;

import java.util.Map;

@JsonSnakeCase
@Value
public class BoardView {

  String sessionId;

  GameState gameState;

  Player nextPlayer;

  Map<Player, PlayerPits> playerPits;

  @JsonSnakeCase
  @Value
  public static class PlayerPits {

    Map<String, Integer> pits;

    int bigPit;
  }
}
