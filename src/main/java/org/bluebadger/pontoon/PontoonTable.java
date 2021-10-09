package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.bluebadger.libraries.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class PontoonTable {
    private static final int MAX_PLAYERS = 6;

    private final Database database;
    private final List<Button> joinRow = new ArrayList<>();
    private final Queue<String> messages = new CircularFifoQueue<>(3);

    private Shuffler shuffler = new Shuffler();
    private Player[] players = new Player[MAX_PLAYERS];
    private String description = "This is where a pretty picture of the table should go";

    private InteractionHook latestHook;

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

        joinRow.add(Button.primary("pontoon-join", "Join"));
        joinRow.add(Button.danger("pontoon-leave", "Leave").asDisabled());
    }

    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("pontoon")) {
            latestHook = event.getHook();

            event.replyEmbeds(buildMainEmbed(), buildChatEmbed())
                    .addActionRow(joinRow)
                    .queue();
        }
    }

    public void onButtonClick(ButtonClickEvent event) {
        switch (event.getComponentId()) {
            case "pontoon-hit":
                description = "HIT";
                break;
            case "pontoon-stand":
                description = "STAND";
                break;
            case "pontoon-split":
                description = "SPLIT";
                break;
            case "pontoon-surrender":
                description = "SURRENDER";
                break;
            case "pontoon-join":
                description = "JOIN";
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
                break;
            case "pontoon-leave":
                description = "LEAVE";
                break;
        }

        if (!event.getComponentId().equals("pontoon-join")) {
            event.editMessageEmbeds(buildMainEmbed()).queue();
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isWebhookMessage()) {
            return;
        }
        // Delete any messages and add it to the message block instead
        Message message = event.getMessage();
        messages.add(message.getContentDisplay());
        message.delete().queue();
        updateView();
    }

    /**
     * Only selection menu for this table is seat selection
     */
    public void onSelectionMenu(SelectionMenuEvent event) {
        int index = Integer.parseInt(event.getValues().get(0));
        Player player = new Player(event.getMember().getUser().getId(), index + 1, 1000);
        players[index] = player;

        // Update leave button to active
        if (joinRow.get(1).isDisabled()) {
            joinRow.set(1, joinRow.get(1).asEnabled());
        }

        event.editMessageEmbeds(player.buildPlayerEmbed()).setActionRow(player.getOptions()).queue();
        updateView();
    }

    private void updateView() {
        latestHook.editOriginalEmbeds(buildMainEmbed(), buildChatEmbed())
                .setActionRow(joinRow)
                .queue();
    }

    private MessageEmbed buildMainEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Pontoon Table");
        eb.setDescription(description);
        // TODO: Display table

        return eb.build();
    }

    private MessageEmbed buildChatEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Chat");

        StringBuilder sb = new StringBuilder();
        messages.forEach(message -> sb.append(String.format("%s%n", message)));
        eb.setDescription(sb.toString());

        return eb.build();
    }
}
