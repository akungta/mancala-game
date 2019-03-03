package com.akashrungta.game.mancala.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Player {
  PLAYER1(0),
  PLAYER2(1);

  int index;

  Player(int index) {
    this.index = index;
  }

  public static Player get(int index) {
    switch (index) {
      case 0:
        return PLAYER1;
      case 1:
        return PLAYER2;
    }
    return null;
  }

  @JsonCreator
  public static Player forValue(String value) {
    return Player.valueOf(value);
  }

  @JsonValue
  public String toValue() {
    return this.name();
  }
}
