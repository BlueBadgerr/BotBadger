package org.bluebadger.pontoon;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import org.bluebadger.libraries.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PontoonTable {
    private static final int MAX_PLAYERS = 6;

    private final Database database;

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

    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("pontoon")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Pontoon Table");
            eb.setDescription("This is where a pretty picture of the table should go");
            // TODO: Display table

            // Build buttons
            List<Component> playerOptionsRow = new ArrayList<>();
            playerOptionsRow.add(Button.danger("pontoon-hit", "Hit"));
            playerOptionsRow.add(Button.success("pontoon-stand", "Stand"));
            playerOptionsRow.add(Button.primary("pontoon-split", "Split"));
            playerOptionsRow.add(Button.secondary("pontoon-surrender", "Split"));

            List<Component> joinRow = new ArrayList<>();
            joinRow.add(Button.success("pontoon-join-1", "Join 1"));
            joinRow.add(Button.success("pontoon-join-2", "Join 2"));
            joinRow.add(Button.success("pontoon-join-3", "Join 3"));
            joinRow.add(Button.success("pontoon-join-4", "Join 4"));
            joinRow.add(Button.success("pontoon-join-5", "Join 5"));
            joinRow.add(Button.success("pontoon-join-6", "Join 6"));

            event.replyEmbeds(eb.build())
                    .addActionRow(playerOptionsRow)
                    .addActionRow(joinRow)
                    .queue();
        }
    }

    public void onButtonClick(ButtonClickEvent event) {

    }
}
