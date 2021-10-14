package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bluebadger.libraries.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Player {
    public enum Move {
        HIT,
        STAND,
        SPLIT,
        SURRENDER
    }

    private final String userId;
    private final int playerIndex;
    private final List<Button> options = new ArrayList<>();
    private final Button exitButton;
    private final BlockingQueue<Move> move = new ArrayBlockingQueue<Move>(1);

    private int gold;
    private Hand hand = new Hand();

    public Player(String userId, int playerIndex) {
        this.userId = userId;
        this.playerIndex = playerIndex;

        options.add(Button.danger(buildButtonId("hit"), "Hit"));
        options.add(Button.success(buildButtonId("stand"), "Stand"));
        options.add(Button.primary(buildButtonId("split"), "Split"));
        options.add(Button.secondary(buildButtonId("surrender"), "Surrender"));

        exitButton = Button.danger(buildButtonId("exit"), "Exit");
    }

    public void onButtonClick(ButtonClickEvent event) {
        // Extract the button option
        String optionSelected = event.getComponentId().split("-")[2];
        event.editMessageEmbeds(buildMessageEmbed(optionSelected)).setActionRows(buildActionRows()).queue();
    }

    /**
     * Receive any selection menu event.
     */
    public void onSelectionMenu(SelectionMenuEvent event) {
        event.editMessageEmbeds(buildMessageEmbed("Select your action")).setActionRows(buildActionRows()).queue();
    }

    /*--- Game control section ---*/
    public int getPlayerIndex() {
        return playerIndex;
    }

    public Move getMove() {
        try {
            return move.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public List<Card> getCards() {
        return hand.cards.get(0);
    }

    public void dealCard(Card card) {
        hand.receiveCard(card);
    }

    private String buildButtonId(String option) {
        return String.format("pontoon-%d-%s", playerIndex, option);
    }

    private List<ActionRow> buildActionRows() {
        List<ActionRow> result = new ArrayList<>();

        result.add(ActionRow.of(buildOptions()));
        result.add(ActionRow.of(exitButton));

        return result;
    }

    private MessageEmbed buildMessageEmbed(String message) {
        return new EmbedBuilder()
                .setTitle(String.format("Seat %d", playerIndex + 1))
                .setDescription(message)
                .build();
    }

    private List<Button> buildOptions() {
        return options;
    }

    private class Hand {
        List<List<Card>> cards = new ArrayList<>();
        int stackIndex;

        private Hand() {
            cards.add(new ArrayList<>());
            stackIndex = 0;
        }

        private void receiveCard(Card card) {
            cards.get(stackIndex).add(card);
        }
    }
}
