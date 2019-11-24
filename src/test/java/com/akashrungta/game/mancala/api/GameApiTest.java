package com.akashrungta.game.mancala.api;

import com.akashrungta.game.mancala.core.BoardView;
import com.akashrungta.game.mancala.core.GameState;
import com.akashrungta.game.mancala.core.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GameApiTest {

    private GameApi gameApi = new GameApi();

    @Test
    public void testBotPlay() {
        Random random = new Random();
        BoardView view = gameApi.start(random.nextBoolean() ? Player.PLAYER1 : Player.PLAYER2);
        System.out.println("Starting with " + view.getNextPlayer());
        int count = 0;
        while (view.getGameState() == GameState.PLAYING) {
            Player nextPlayer = view.getNextPlayer();
            BoardView.PlayerPits pits = view.getPlayerPits().get(nextPlayer);
            List<String> nonZeroPositions = pits.getPits().entrySet().stream().filter(e -> e.getValue() != 0).map(Map.Entry::getKey).collect(Collectors.toList());
            String randomPosition = nonZeroPositions.get(random.nextInt(nonZeroPositions.size()));
            view = gameApi.play(view.getSessionId(), nextPlayer, Integer.parseInt(randomPosition));
            count++;
        }
        BoardView.PlayerPits player1Pits = view.getPlayerPits().get(Player.PLAYER1);
        BoardView.PlayerPits player2Pits = view.getPlayerPits().get(Player.PLAYER2);
        if (view.getGameState() == GameState.PLAYER1_WIN) {
            Assert.assertTrue(player1Pits.getBigPit() > player2Pits.getBigPit());
        } else if (view.getGameState() == GameState.PLAYER2_WIN) {
            Assert.assertTrue(player2Pits.getBigPit() > player1Pits.getBigPit());
        } else {
            Assert.assertTrue(player2Pits.getBigPit() == player1Pits.getBigPit());
        }
        System.out.println(view.getGameState() + " with plays " + count);
    }

    @Test
    public void testManualPlay() {
        BoardView view = gameApi.start(Player.PLAYER1);
        String sessionId = view.getSessionId();
        Assert.assertEquals("6,6,6,6,6,6{0}", checkPlayer1(view));
        Assert.assertEquals("{0}6,6,6,6,6,6", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER1, 0);
        Assert.assertEquals("0,7,7,7,7,7{1}", checkPlayer1(view));
        Assert.assertEquals("{0}6,6,6,6,6,6", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER1, 1);
        Assert.assertEquals("0,0,8,8,8,8{2}", checkPlayer1(view));
        Assert.assertEquals("{0}6,6,6,6,7,7", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER2, 5);
        Assert.assertEquals("1,1,9,9,9,8{2}", checkPlayer1(view));
        Assert.assertEquals("{1}0,6,6,6,7,7", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER1, 0);
        Assert.assertEquals("0,2,9,9,9,8{2}", checkPlayer1(view));
        Assert.assertEquals("{1}0,6,6,6,7,7", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER2, 0);
        Assert.assertEquals("1,2,9,9,9,8{2}", checkPlayer1(view));
        Assert.assertEquals("{2}1,7,7,7,8,0", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER1, 0);
        Assert.assertEquals("0,3,9,9,9,8{2}", checkPlayer1(view));
        Assert.assertEquals("{2}1,7,7,7,8,0", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER2, 5);
        Assert.assertEquals("0,3,9,9,9,8{2}", checkPlayer1(view));
        Assert.assertEquals("{3}0,7,7,7,8,0", checkPlayer2(view));
        view = gameApi.play(sessionId, Player.PLAYER2, 4);
        Assert.assertEquals("1,4,10,10,10,8{2}", checkPlayer1(view));
        Assert.assertEquals("{4}1,0,7,7,8,0", checkPlayer2(view));
    }

    private String checkPlayer1(BoardView v) {
        StringBuilder sb = new StringBuilder();
        BoardView.PlayerPits pit1 = v.getPlayerPits().get(Player.PLAYER1);
        sb.append(pit1.getPits().values().stream().map(String::valueOf).collect(Collectors.joining(",")));
        sb.append("{" + pit1.getBigPit() + "}");
        return sb.toString();
    }

    private String checkPlayer2(BoardView v) {
        StringBuilder sb = new StringBuilder();
        BoardView.PlayerPits pit1 = v.getPlayerPits().get(Player.PLAYER2);
        sb.append(pit1.getPits().values().stream().map(String::valueOf).collect(Collectors.joining(",")));
        sb.append("}" + pit1.getBigPit() + "{");
        return sb.reverse().toString();
    }

}