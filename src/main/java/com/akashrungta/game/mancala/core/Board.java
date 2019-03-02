package com.akashrungta.game.mancala.core;

public class Board {

    private static final int PITS_SIZE = 6;

    private static final int NUM_STONES = 6;

    PlayPit[][] playsPits = new PlayPit[2][PITS_SIZE];

    BigPit[] bigPits = new BigPit[2];

    public Board() {

        bigPits[0] = new BigPit(0, 0);
        bigPits[1] = new BigPit(1, 0);

        Pit previousPit = bigPits[1];
        // set up other pits
        for (int player = 0; player < 2; player++) {
            for (int i = 0; i < PITS_SIZE; i++) {
                PlayPit currentPit = new PlayPit(player, i, NUM_STONES);
                playsPits[player][i] = currentPit;
                previousPit.nextPit = currentPit;
                previousPit = currentPit;
            }
            BigPit currentPit = bigPits[player];
            previousPit.nextPit = currentPit;
            previousPit = currentPit;
        }

    }

    public boolean move(int player, int position) {
        Pit pit = playsPits[player][position];
        if (pit.stones == 0) {
            throw new RuntimeException("Not stones");
        } else if (pit instanceof BigPit) {
            throw new RuntimeException("Not allowed to move from Big Pit");
        }

        int stones = pit.stones;
        pit.stones = 0;
        while (stones > 0) {
            pit = pit.nextPit;
            if (pit instanceof BigPit && pit.player != player) {
                continue;
            }
            if(pit instanceof PlayPit && stones == 1 && pit.player == player){
                PlayPit oppositePlayerPit = playsPits[oppositePlayer(player)][oppositePlayerPosition(((PlayPit) pit).position)];
                playsPits[player][PITS_SIZE].stones += 1 + oppositePlayerPit.stones;
                oppositePlayerPit.stones = 0;
                stones--;
            } else {
                pit.stones += 1;
                stones--;
            }
        }
        if (pit instanceof BigPit && pit.player == player) {
            return true;
        }
        return false;
    }

    private int oppositePlayer(int player){
        return player == 1 ? 0 : 1;
    }

    private int oppositePlayerPosition(int position){
        return PITS_SIZE - 1 - position;
    }


    static abstract class Pit {
        int player;
        int stones;
        Pit nextPit;

        public Pit(int player, int stones) {
            this.player = player;
            this.stones = stones;
        }
    }

    static class PlayPit extends Pit {

        int position;

        public PlayPit(int player, int position, int stones) {
            super(player, stones);
            this.position = position;
        }
    }

    static class BigPit extends Pit {

        public BigPit(int player, int stones) {
            super(player, stones);
        }
    }


}
