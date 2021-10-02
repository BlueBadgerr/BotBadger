package org.bluebadger.libraries;

import java.sql.*;

public class Database {

    public static void connect(String databaseUrl) throws SQLException {
        database = new Database(databaseUrl);
    }

    public static Database getInstance() {
        return database;
    }

    private static final String GET_SINGLE_VALUE_BY_ID_QUERY = "SELECT id, %s FROM discord WHERE id = '%s'";

    private static Database database;

    public Integer getInt(String id, String field) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format(GET_SINGLE_VALUE_BY_ID_QUERY, field, id));

        Integer result = null;

        if (resultSet.next()) {
            result = resultSet.getInt(field);
        }

        // Close statement to release resource
        statement.close();

        return result;
    }

    public void setInt(String id, String field, int value) throws SQLException {
        Statement statement = connection.createStatement();

        // Attempt to update value
        String query = String.format("UPDATE discord SET %s = '%d' WHERE id = '%s'", field, value, id);
        int totalUpdated = statement.executeUpdate(query);

        // Failed to update, implies that id does not exist so we insert new row
        if (totalUpdated == 0) {
            query = "INSERT INTO discord(id, %s) VALUES(%s, %d)";
            statement.executeUpdate(String.format(query, field, id, value));
        }

        // Close statement to release resource
        statement.close();
    }

    private final Connection connection;

    private Database(String databaseUrl) throws SQLException {
        connection = DriverManager.getConnection(databaseUrl);
    }
}
