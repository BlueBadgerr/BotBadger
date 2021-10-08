package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.bluebadger.libraries.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PontoonTable {
    private static final int MAX_PLAYERS = 6;

    private final Database database;
    private final List<Component> playerOptionsRow = new ArrayList<>();
    private final List<Component> joinRow = new ArrayList<>();

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

        playerOptionsRow.add(Button.danger("pontoon-hit", "Hit"));
        playerOptionsRow.add(Button.success("pontoon-stand", "Stand"));
        playerOptionsRow.add(Button.primary("pontoon-split", "Split"));
        playerOptionsRow.add(Button.secondary("pontoon-surrender", "Surrender"));

        joinRow.add(Button.primary("pontoon-join", "Join"));
        joinRow.add(Button.danger("pontoon-leave", "Leave"));
    }

    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("pontoon")) {
            latestHook = event.getHook();

            event.replyEmbeds(buildEmbed())
                    .addActionRow(playerOptionsRow)
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
                event.replyEmbeds(msg)
                        .setEphemeral(true)
                        .addActionRow(SelectionMenu.create("pontoon-seat-select")
                                .addOption("1", "1")
                                .addOption("2", "2")
                                .addOption("3", "3")
                                .addOption("4", "4")
                                .addOption("5", "5")
                                .addOption("6", "6")
                                .build())
                        .queue();
                break;
            case "pontoon-leave":
                description = "LEAVE";
                break;
        }

        if (!event.getComponentId().equals("pontoon-join")) {
            event.editMessageEmbeds(buildEmbed()).queue();
        }
    }

    private MessageEmbed buildEmbed() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Pontoon Table");
        eb.setDescription(description);
        // TODO: Display table

        return eb.build();
    }
}
