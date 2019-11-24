package com.akashrungta.game.mancala.core;

import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

    @Test
    public void testBoardSetup() {
        Board board = new Board(Player.PLAYER1, "id");
        Assert.assertEquals(Player.PLAYER1, board.getNextPlayer());
        BoardView view = board.getView();
        BoardView.PlayerPits player1Pits = view.getPlayerPits().get(Player.PLAYER1);
        BoardView.PlayerPits player2Pits = view.getPlayerPits().get(Player.PLAYER2);
        Assert.assertEquals(0, player1Pits.getBigPit());
        player1Pits.getPits().values().forEach(v -> Assert.assertEquals(6, (int) v));
        player2Pits.getPits().values().forEach(v -> Assert.assertEquals(6, (int) v));
    }

    @Test
    public void testPlayer1Play() {
        Board board = new Board(Player.PLAYER1, "id");
        board.play(Player.PLAYER1, 0);
        BoardView view = board.getView();
        // check if player 1 pits following position 0 has +1 stones
        BoardView.PlayerPits player1Pits = view.getPlayerPits().get(Player.PLAYER1);
        BoardView.PlayerPits player2Pits = view.getPlayerPits().get(Player.PLAYER2);
        Assert.assertEquals(0, (int) player1Pits.getPits().get("0"));
        Assert.assertEquals(7, (int) player1Pits.getPits().get("1"));
        Assert.assertEquals(7, (int) player1Pits.getPits().get("2"));
        Assert.assertEquals(7, (int) player1Pits.getPits().get("3"));
        Assert.assertEquals(7, (int) player1Pits.getPits().get("4"));
        Assert.assertEquals(7, (int) player1Pits.getPits().get("5"));
        Assert.assertEquals(1, player1Pits.getBigPit());
        // no change in player 2 stones
        player2Pits.getPits().values().forEach(v -> Assert.assertEquals(6, (int) v));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSamePlayerException() {
        Board board = new Board(Player.PLAYER1, "id");
        try {
            board.play(Player.PLAYER1, 3);
            Assert.assertEquals(Player.PLAYER2, board.getNextPlayer());
            board.play(Player.PLAYER1, 1);
        } catch (Throwable t) {
            Assert.assertEquals("Wrong Player's turn", t.getMessage());
            throw t;
        }
    }

    @Test
    public void testSameNextPlayer() {
        Board board = new Board(Player.PLAYER1, "id");
        // last stones in the same player's big pit, one more turn
        board.play(Player.PLAYER1, 0);
        Assert.assertEquals(Player.PLAYER1, board.getNextPlayer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectPosition() {
        Board board = new Board(Player.PLAYER1, "id");
        try {
            board.play(Player.PLAYER1, 8);
        } catch (Throwable t) {
            Assert.assertEquals("Invalid position", t.getMessage());
            throw t;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test0StonePlay() {
        Board board = new Board(Player.PLAYER1, "id");
        try {
            board.play(Player.PLAYER1, 0);
            board.play(Player.PLAYER1, 1);
            board.play(Player.PLAYER2, 0);
            board.play(Player.PLAYER1, 1);
        } catch (Throwable t) {
            Assert.assertEquals("No stones in the pit", t.getMessage());
            throw t;
        }
    }

}
