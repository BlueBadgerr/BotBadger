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
import org.bluebadger.interfaces.Action;
import org.bluebadger.libraries.Database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Queue;

public class PontoonTable implements Action {
    private static final int MAX_PLAYERS = 6;

    private final Database database;
    private final ViewManager viewManager = new ViewManager();
    private final Queue<String> chatHistory = new CircularFifoQueue<>(3);

    private Shuffler shuffler = new Shuffler();
    private Player[] players = new Player[MAX_PLAYERS];
    private String description = "This is where a pretty picture of the table should go";

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
        viewManager.setHook(event.getHook());
        viewManager.update(event);
    }

    public void apply(ButtonClickEvent event) {
        switch (event.getComponentId()) {
            case "pontoon-hit":
            case "pontoon-stand":
            case "pontoon-split":
            case "pontoon-surrender":
            case "pontoon-main-leave":
                System.out.println(event.getComponentId());
                description = event.getComponentId();
                viewManager.update();
                break;
            case "pontoon-main-join":
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
        chatHistory.add(String.format("%s: %s", message.getMember().getEffectiveName(), message.getContentDisplay()));
        message.delete().queue();
        viewManager.update();
    }

    /**
     * Only selection menu for this table is seat selection
     */
    public void apply(SelectionMenuEvent event) {
        int index = Integer.parseInt(event.getValues().get(0));
        Player player = new Player(event.getMember().getUser().getId(), index + 1, 1000);
        players[index] = player;

        player.onSelectionMenu(event);

        // Update leave button to active
        viewManager.enableLeave(true);
        viewManager.update();
    }

    private String buttonId(String viewCategory, String buttonId) {
        return String.format("pontoon-%s-%s", viewCategory, buttonId);
    }

    private class ViewManager {
        public static final String VIEW_CATEGORY = "main";
        private Button joinButton = Button.primary(buttonId(VIEW_CATEGORY, "join"), "Join");
        private Button leaveButton = Button.danger(buttonId(VIEW_CATEGORY, "leave"), "Leave");

        private InteractionHook hook;

        public ViewManager() {
            leaveButton = leaveButton.asDisabled();
        }

        public void setHook(InteractionHook hook) {
            this.hook = hook;
        }

        public void update() {
            hook.editOriginalEmbeds(buildMainEmbed(), buildChatEmbed())
                    .setActionRow(Arrays.asList(joinButton, leaveButton))
                    .queue();
        }

        /**
         * First call we want to directly reply to event instead of using hook
         */
        public void update(SlashCommandEvent event) {
            event.replyEmbeds(buildMainEmbed(), buildChatEmbed())
                    .addActionRow(Arrays.asList(joinButton, leaveButton))
                    .queue();
        }

        public void enableJoin(boolean enable) {
            if (enable && joinButton.isDisabled()) {
                joinButton = joinButton.asEnabled();
            }

            if (!enable && !joinButton.isDisabled()) {
                joinButton = joinButton.asDisabled();
            }
        }

        public void enableLeave(boolean enable) {
            if (enable && leaveButton.isDisabled()) {
                leaveButton = leaveButton.asEnabled();
            }

            if (!enable && !leaveButton.isDisabled()) {
                leaveButton = leaveButton.asDisabled();
            }
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
            chatHistory.forEach(message -> sb.append(String.format("%s%n", message)));
            eb.setDescription(sb.toString());

            return eb.build();
        }
    }
}
