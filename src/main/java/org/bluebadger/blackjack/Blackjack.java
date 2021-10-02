package org.bluebadger.blackjack;

import org.bluebadger.libraries.Database;

import java.sql.SQLException;

public class Blackjack {
    private final Database database;

    public void add(String id) {
        try {
            Integer result = database.getInt(id, "yoru_slap_count");
            int newValue = result + 1;
            database.setInt(id, "yoru_slap_count", newValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer get(String id) {
        try {
            return database.getInt(id, "yoru_slap_count");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Blackjack() {
        database = Database.getDatabase();
    }
}
