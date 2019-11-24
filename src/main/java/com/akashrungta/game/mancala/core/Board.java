package com.akashrungta.game.mancala.core;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board {

  private static final int PITS_SIZE = 6;

  private static final int NUM_STONES = 6;

  private Map<Player, PlayPit[]> playPits = new HashMap<>();

  private Map<Player, BigPit> bigPits = new HashMap<>();

  @Getter private Player nextPlayer;

  private GameState gameState = GameState.PLAYING;

  @Getter private boolean isGameFinished;

  private String sessionId;

  public Board(Player nextPlayer, String sessionId) {
    this.nextPlayer = nextPlayer;
    this.sessionId = sessionId;

    bigPits.put(Player.PLAYER1, new BigPit(Player.PLAYER1, 0));
    bigPits.put(Player.PLAYER2, new BigPit(Player.PLAYER2, 0));

    // set the player 2 big pit for circular linked list
    Pit previousPit = bigPits.get(Player.PLAYER2);
    // set up the board initial state for player 1
    previousPit = setupPits(Player.PLAYER1, previousPit);
    // set up the board initial state for player 2
    setupPits(Player.PLAYER2, previousPit);

    // set up opposite pits
    for (int position = 0; position < PITS_SIZE; position++) {
      PlayPit player1Pit = playPits.get(Player.PLAYER1)[position];
      // the opponent's pit would be diagonally opposite
      PlayPit player2Pit = playPits.get(Player.PLAYER2)[PITS_SIZE - 1 - position];
      player1Pit.oppositePit = player2Pit;
      player2Pit.oppositePit = player1Pit;
    }
  }

  private Pit setupPits(Player player, Pit previousPit) {
    PlayPit[] pits = new PlayPit[PITS_SIZE];
    for (int position = 0; position < PITS_SIZE; position++) {
      PlayPit currentPit = new PlayPit(player, NUM_STONES, position);
      pits[position] = currentPit;
      previousPit.nextPit = currentPit;
      previousPit = currentPit;
    }
    playPits.put(player, pits);
    BigPit currentPit = bigPits.get(player);
    previousPit.nextPit = currentPit;
    previousPit = currentPit;
    return previousPit;
  }

  public BoardView getView() {
    Map<Player, BoardView.PlayerPits> playerPitsView = new LinkedHashMap<>();
    for (Player player : playPits.keySet()) {
      Map<String, Integer> pitsView = new LinkedHashMap<>();
      for (PlayPit playPit : playPits.get(player)) {
        pitsView.put(String.valueOf(playPit.position), playPit.stones);
      }
      playerPitsView.put(player, new BoardView.PlayerPits(pitsView, bigPits.get(player).stones));
    }
    return new BoardView(sessionId, gameState, nextPlayer, playerPitsView);
  }

  public void play(Player player, int position) {

    Preconditions.checkArgument(nextPlayer == player, "Wrong Player's turn");
    Preconditions.checkArgument(position >= 0 && position < PITS_SIZE, "Invalid position");

    // Figure out the pit the position maps to
    Pit pit = playPits.get(player)[position];

    // throw exceptions if there are no stones in the pit or its the big pit
    Preconditions.checkArgument(pit.stones > 0, "No stones in the pit");

    // number of stones that can be sowed
    int stones = pit.stones;

    // stones in current pit becomes 0
    pit.stones = 0;

    // sow stones until they are gone
    while (stones > 0) {

      pit = pit.nextPit;

      // skip sowing in the opponent player's big pit
      if (pit instanceof BigPit && pit.player != player) {
        continue;
      }

      // capture opponent's stones if last stone lands in an own empty pit
      if (pit.stones == 0 && stones == 1 && pit.player == player && pit instanceof PlayPit) {
        captureOpponentPlayerStones((PlayPit) pit);
      }

      // sow a stone
      pit.stones += 1;

      // reduce from the pile
      stones--;
    }

    // if the last stone lands on own big pit, player get another turn
    if (pit instanceof BigPit && pit.player == player) {
      nextPlayer = player;
    } else {
      nextPlayer = opponentPlayer(player);
    }

    checkGameFinish();
  }

  private void checkGameFinish() {
    for (Player player : playPits.keySet()) {
      if (Arrays.stream(playPits.get(player)).allMatch(p -> p.stones == 0)) {
        // game over
        isGameFinished = true;
        break;
      }
    }
    if (isGameFinished) {

      // clean-up pits
      for (Player player : playPits.keySet()) {
        BigPit bigPit = bigPits.get(player);
        Arrays.stream(playPits.get(player))
            .forEach(
                p -> {
                  bigPit.stones += p.stones;
                  p.stones = 0;
                });
      }

      if (bigPits.get(Player.PLAYER1).stones > bigPits.get(Player.PLAYER2).stones) {
        gameState = GameState.PLAYER1_WIN;
      } else if (bigPits.get(Player.PLAYER1).stones < bigPits.get(Player.PLAYER2).stones) {
        gameState = GameState.PLAYER2_WIN;
      } else {
        gameState = GameState.TIE;
      }
    }
  }

  private void captureOpponentPlayerStones(PlayPit pit) {
    pit.stones += pit.oppositePit.stones;
    pit.oppositePit.stones = 0;
  }

  private Player opponentPlayer(Player player) {
    return player == Player.PLAYER1 ? Player.PLAYER2 : Player.PLAYER1;
  }

  private abstract static class Pit {
    Player player;
    int stones;
    Pit nextPit;

    Pit(Player player, int stones) {
      this.player = player;
      this.stones = stones;
    }
  }

  private static class PlayPit extends Pit {

    int position;

    PlayPit oppositePit;

    PlayPit(Player player, int stones, int position) {
      super(player, stones);
      this.position = position;
    }
  }

  private static class BigPit extends Pit {

    BigPit(Player player, int stones) {
      super(player, stones);
    }
  }
}
