import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.net.UnknownServiceException;
import java.sql.*;
import java.util.List;

public class Bot extends ListenerAdapter {
    private static final String INSERT_QUERY = "INSERT INTO discord(id, yoru_slap_count) VALUES(?, ?)";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM discord";
    private static final String SELECT_USER_QUERY = "SELECT * FROM discord WHERE id = '?'";

    private static JDA jda;
    private static Connection connection;
    private static Summoner summoner;

    public static void main(String[] args) throws LoginException, SQLException {
        String token = System.getenv("TOKEN");

        jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("you"))
                .build();

        // Bot action classes
        summoner = new Summoner();

        // Database
        connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
//        Statement statement = conn.createStatement();
//        ResultSet resultSet = statement.executeQuery(SELECT_ALL_QUERY);

        // This can take up to 1 hour to show up in the client
        jda.updateCommands()
                .addCommands(new CommandData("summon-heroes", "Performs a 10x summon"))
                .queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        switch (event.getName()) {
            case "summon-heroes":
                summoner.onSlashCommand(event);
                break;
            default:
                System.out.printf("Unknown slash command %s%n", event.getName());
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        switch (event.getComponentId()) {
            case "reroll":
                summoner.onButtonClick(event);
                break;
            default:
                System.out.printf("Unknown button press %s%n", event.getComponentId());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();

        if (msg.getContentRaw().equals("add")) {
            try {
                Integer slapCount = getYoruSlapCount("122354953162457088");
                if (slapCount == null) {
                    PreparedStatement insertStatement = connection.prepareStatement(INSERT_QUERY);

                    insertStatement.setString(1, msg.getMember().getUser().getId());
                    insertStatement.setInt(2, 1);
                    insertStatement.executeUpdate();
                } else {
                    Statement statement = connection.createStatement();
                    slapCount++;
                    statement.executeUpdate(
                            String.format("UPDATE discord SET yoru_slap_count = '%d' WHERE id = '122354953162457088'", slapCount));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (msg.getContentRaw().equals("get")) {
            try {
                System.out.println("Total slap = " + getYoruSlapCount("122354953162457088"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Used to reply to messages
//        MessageChannel channel = event.getChannel();
//        long time = System.currentTimeMillis();
//
//        EmbedBuilder eb = new EmbedBuilder();
//        eb.setTitle("Pong");
//        eb.setDescription("HAHAHAHA");
//
//        channel.sendMessageEmbeds(eb.build()).queue();
    }

    private Integer getYoruSlapCount(String user) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM discord WHERE id = '%s'", user));

        if (resultSet.next()) {
            return resultSet.getInt("yoru_slap_count");
        }

        return null;
    }
}
