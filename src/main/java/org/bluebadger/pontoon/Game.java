package org.bluebadger.pontoon;

import org.bluebadger.libraries.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Game {
    private final List<Card> dealer = new ArrayList<>();
    private final Map<Integer, Player> players = new TreeMap<>();
    private final Shuffler shuffler = new Shuffler();
    private final View view;

    private State state;

    public Game(View view) {
        this.view = view;
        state = State.WAITING_FOR_PLAYERS;
    }

    public void addPlayer(Player player) {
        players.put(player.getPlayerIndex(), player);

        if (state == State.WAITING_FOR_PLAYERS) {
            newRound();
        }
    }

    private void removePlayer(int index) {
        players.remove(index);
    }

    private void newRound() {
        state = State.NEW_ROUND;

        // Deal two cards to everyone, one at a time in a clockwise direction
        for (int i = 0; i < 2; i++) {
            players.forEach((index, player) -> player.dealCard(shuffler.draw()));
            dealer.add(shuffler.draw());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Dealer: ");
        dealer.forEach(card -> sb.append(card.getRank() + " "));

        players.forEach((index, player) -> {
            sb.append(String.format("%nPlayer %d: ", player.getPlayerIndex() + 1));
            player.getCards().forEach(card -> sb.append(card.getRank() + " "));
        });

        view.updateDescription(sb.toString());
        view.update();
    }

    // Defines the game state
    private enum State {
        WAITING_FOR_PLAYERS,
        NEW_ROUND,
        PLAYER_TURN,
        END_ROUND
    }
}
