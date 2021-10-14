package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Queue;

public class View {
    private final Queue<String> chatHistory = new CircularFifoQueue<>(3);

    private InteractionHook hook;
    private Button joinButton = Button.primary("pontoon-main-join", "Join");

    // TODO: remove and replace with a proper description
    private String description = "This is where a pretty picture of the table should go";

    public void setHook(InteractionHook hook) {
        this.hook = hook;
    }

    public void update() {
        hook.editOriginalEmbeds(buildMainEmbed(), buildChatEmbed())
                .setActionRow(joinButton)
                .queue();
    }

    /**
     * First call we want to directly reply to event instead of using hook
     */
    public void update(SlashCommandEvent event) {
        event.replyEmbeds(buildMainEmbed(), buildChatEmbed())
                .addActionRow(joinButton)
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

    public void updateDescription(String description) {
        this.description = description;
    }

    public void appendChat(String line) {
        chatHistory.add(line);
        update();
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
