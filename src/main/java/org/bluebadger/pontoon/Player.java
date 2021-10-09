package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.Button;
import org.bluebadger.libraries.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String userId;
    private final List<Button> options = new ArrayList<>();

    private int seatNumber;
    private long gold;
    private Card[] cards = new Card[2];

    public Player(String userId, int seatNumber, long gold) {
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.gold = gold;

        options.add(Button.danger("pontoon-hit", "Hit"));
        options.add(Button.success("pontoon-stand", "Stand"));
        options.add(Button.primary("pontoon-split", "Split"));
        options.add(Button.secondary("pontoon-surrender", "Surrender"));
    }

    public MessageEmbed buildPlayerEmbed() {
        return new EmbedBuilder()
                .setTitle(String.format("Seat %d", seatNumber))
                .setDescription("Select your action")
                .build();
    }

    public List<Button> getOptions() {
        return options;
    }
}
