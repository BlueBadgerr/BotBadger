package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bluebadger.libraries.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String userId;
    private final int playerIndex;
    private final List<Button> options = new ArrayList<>();

    private long gold;
    private Card[] cards = new Card[2];

    public Player(String userId, int playerIndex, long gold) {
        this.userId = userId;
        this.playerIndex = playerIndex;
        this.gold = gold;

        options.add(Button.danger(buildButtonId("hit"), "Hit"));
        options.add(Button.success(buildButtonId("stand"), "Stand"));
        options.add(Button.primary(buildButtonId("split"), "Split"));
        options.add(Button.secondary(buildButtonId("surrender"), "Surrender"));
    }

    public void onButtonClick(ButtonClickEvent event) {
        // Extract the button option
        String optionSelected = event.getComponentId().split("-")[2];
        event.editMessageEmbeds(buildMessageEmbed(optionSelected)).setActionRow(buildOptions()).queue();
    }

    /**
     * Receive any selection menu event.
     */
    public void onSelectionMenu(SelectionMenuEvent event) {
        event.editMessageEmbeds(buildMessageEmbed("Select your action")).setActionRow(buildOptions()).queue();
    }

    private String buildButtonId(String option) {
        return String.format("pontoon-%d-%s", playerIndex, option);
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
}
