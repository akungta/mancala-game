package com.akashrungta.game.mancala.core;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board {

  public static final int PITS_SIZE = 6;

  public static final int NUM_STONES = 6;

  private Map<Player, PlayPit[]> playPits = new HashMap<>();

  private BigPit[] bigPits = new BigPit[2];

  @Getter private Player nextPlayer;

  private GameState gameState = GameState.PLAYING;

  @Getter private boolean isGameFinished;

  private String sessionId;

  public Board(Player nextPlayer, String sessionId) {
    this.nextPlayer = nextPlayer;
    this.sessionId = sessionId;

    bigPits[0] = new BigPit(Player.PLAYER1, 0);
    bigPits[1] = new BigPit(Player.PLAYER2, 0);

    Pit previousPit = bigPits[1];
    // set up the board initial state
    for (int playerIndex = 0; playerIndex < 2; playerIndex++) {
      PlayPit[] playPits = new PlayPit[PITS_SIZE];
      for (int i = 0; i < PITS_SIZE; i++) {
        PlayPit currentPit = new PlayPit(Player.get(playerIndex), i, NUM_STONES);
        playPits[i] = currentPit;
        previousPit.nextPit = currentPit;
        previousPit = currentPit;
      }
      this.playPits.put(Player.get(playerIndex), playPits);
      BigPit currentPit = bigPits[playerIndex];
      previousPit.nextPit = currentPit;
      previousPit = currentPit;
    }
  }

  public BoardView getView() {
    Map<Player, BoardView.PlayerPits> playerPitsView = new LinkedHashMap<>();
    for (Player player : playPits.keySet()) {
      Map<Integer, Integer> pitsView = new LinkedHashMap<>();
      for (PlayPit playPit : playPits.get(player)) {
        pitsView.put(playPit.position, playPit.stones);
      }
      playerPitsView.put(player, new BoardView.PlayerPits(pitsView, bigPits[player.index].stones));
    }
    return new BoardView(sessionId, gameState, nextPlayer, playerPitsView);
  }

  public void play(Player player, int position) {
    // Figure out the pit the position maps to
    Pit pit = playPits.get(player)[position];

    // throw exceptions if there are no stones in the pit or its the big pit
    if (pit.stones == 0) {
      throw new RuntimeException("No stones in the pit");
    }

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
      if (Arrays.stream(playPits.get(player)).mapToInt(p -> p.stones).sum() == 0) {
        // game over
        isGameFinished = true;
        break;
      }
    }
    if (isGameFinished) {

      // clean-up pits
      for (Player player : playPits.keySet()) {
        BigPit bigPit = bigPits[player.index];
        Arrays.stream(playPits.get(player))
            .forEach(
                p -> {
                  bigPit.stones += p.stones;
                  p.stones = 0;
                });
      }

      if (bigPits[0].stones > bigPits[1].stones) {
        gameState = GameState.PLAYER1_WIN;
      } else if (bigPits[0].stones < bigPits[1].stones) {
        gameState = GameState.PLAYER2_WIN;
      } else {
        gameState = GameState.TIE;
      }
    }
  }

  private void captureOpponentPlayerStones(PlayPit pit) {
    PlayPit oppositePlayerPit =
        playPits.get(opponentPlayer(pit.player))[opponentPlayerPosition(pit.position)];
    pit.stones += oppositePlayerPit.stones;
    oppositePlayerPit.stones = 0;
  }

  private Player opponentPlayer(Player player) {
    return player == Player.PLAYER1 ? Player.PLAYER2 : Player.PLAYER1;
  }

  // the opponent's pit would be diagonally opposite
  private int opponentPlayerPosition(int position) {
    return PITS_SIZE - 1 - position;
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

    PlayPit(Player player, int position, int stones) {
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
