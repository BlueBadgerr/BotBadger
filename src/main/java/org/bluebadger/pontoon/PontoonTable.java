package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.bluebadger.interfaces.Action;
import org.bluebadger.libraries.Database;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class PontoonTable implements Action {
    private static final int MAX_PLAYERS = 6;

    private final Database database;
    private final View view;
    private final Game game;

    private Player[] players = new Player[MAX_PLAYERS];

    public PontoonTable() {
        database = Database.getInstance();
        this.view = new View();
        this.game = new Game(view);
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
        Player player = new Player(event.getMember().getUser().getId(), index);
        players[index] = player;
        game.addPlayer(player);

        player.onSelectionMenu(event);

        if (Arrays.stream(players).filter(Objects::nonNull).count() == MAX_PLAYERS) {
            view.enableJoin(false);
        }

        view.update();
    }
}
