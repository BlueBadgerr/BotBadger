package org.bluebadger.pontoon;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.bluebadger.libraries.Database;

import java.sql.SQLException;

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

    }

    public void onButtonClick(ButtonClickEvent event) {

    }
}
