package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.bluebadger.interfaces.Action;
import org.bluebadger.libraries.Database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.regex.Pattern;

public class PontoonTable implements Action {
    private static final int MAX_PLAYERS = 6;

    private final Database database;
    private final View view = new View();

    private Shuffler shuffler = new Shuffler();
    private Player[] players = new Player[MAX_PLAYERS];

    // Test only
    public void add(String id) {
        try {
            Integer result = database.getInt(id, "yoru_slap_count");
            int newValue = result + 1;
            database.setInt(id, "yoru_slap_count", newValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Test only
    public Integer get(String id) {
        try {
            return database.getInt(id, "yoru_slap_count");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Test only
    public String draw() {
        return shuffler.draw().toString();
    }

    public PontoonTable() {
        database = Database.getInstance();
    }

    public void apply(SlashCommandEvent event) {
        view.setHook(event.getHook());
        view.update(event);
    }

    public void apply(ButtonClickEvent event) {
        Pattern pattern = Pattern.compile("\\d");
        String target = event.getComponentId().split("-")[1];

        if (pattern.matcher(target).matches()) {
            int index = Integer.parseInt(target);
            players[index].onButtonClick(event);
        } else {
            // The only button remaining is join
            view.updateDescription("New player joined");

            MessageEmbed msg = new EmbedBuilder()
                    .setTitle("Join Pontoon Table")
                    .setDescription("Select a seat to join")
                    .build();

            SelectionMenu.Builder selectionMenuBuilder = SelectionMenu.create("pontoon-seatSelect");

            for (int i = 0; i < players.length; i++) {
                if (players[i] == null) {
                    selectionMenuBuilder.addOption(Integer.toString(i+1), Integer.toString(i));
                }
            }

            event.replyEmbeds(msg)
                    .setEphemeral(true)
                    .addActionRow(selectionMenuBuilder.build())
                    .queue();
        }
    }

    /**
     * Only message we are expecting is talking
     */
    public void apply(MessageReceivedEvent event) {
        if (event.isWebhookMessage()) {
            return;
        }
        // Delete any messages and add it to the message block instead
        Message message = event.getMessage();
        view.appendChat(String.format("%s: %s", message.getMember().getEffectiveName(), message.getContentDisplay()));
        message.delete().queue();
        view.update();
    }

    /**
     * Only selection menu for this table is seat selection
     */
    public void apply(SelectionMenuEvent event) {
        int index = Integer.parseInt(event.getValues().get(0));
        Player player = new Player(event.getMember().getUser().getId(), index, 1000);
        players[index] = player;

        player.onSelectionMenu(event);

        if (Arrays.stream(players).filter(Objects::nonNull).count() == MAX_PLAYERS) {
            view.enableJoin(false);
        }

        view.update();
    }
}
