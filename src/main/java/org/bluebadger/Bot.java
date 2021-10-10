package org.bluebadger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bluebadger.interfaces.Action;
import org.bluebadger.libraries.Database;
import org.bluebadger.pontoon.PontoonTable;
import org.bluebadger.summoner.Summoner;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Bot extends ListenerAdapter {
    private static final boolean NEW_SLASH_COMMAND = false;
    private static final Map<String, Action> actionMap = new HashMap<>();

    private static String pontoonChannelId = null;

    public static void main(String[] args) throws LoginException, SQLException {
        String token = System.getenv("TOKEN");

        JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("you"))
                .build();

        // Connect to Postgres database
        Database.connect(System.getenv("JDBC_DATABASE_URL"));

        // Bot action classes
        actionMap.put("summoner", new Summoner());
        actionMap.put("pontoon", new PontoonTable());

        // This can take up to 1 hour to show up in the client
        if (NEW_SLASH_COMMAND) {
            jda.updateCommands()
                    .addCommands(new CommandData("summon-heroes", "Performs a 10x summon"))
                    .addCommands(new CommandData("pontoon", "Play a game of Pontoon"))
                    .queue();
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        switch (event.getName()) {
            case "summon-heroes":
                actionMap.get("summoner").apply(event);
                break;
            case "pontoon":
                pontoonChannelId = event.getChannel().getId();
                actionMap.get("pontoon").apply(event);
                break;
            default:
                System.out.printf("Unknown slash command %s%n", event.getName());
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        String id = event.getComponentId().split("-")[0];

        if (actionMap.containsKey(id)) {
            actionMap.get(id).apply(event);
        } else {
            System.out.println("Unrecognised button pressed: " + id);
        }
    }

    @Override
    public void onSelectionMenu(SelectionMenuEvent event) {
        String id = event.getComponentId().split("-")[0];

        if (actionMap.containsKey(id)) {
            actionMap.get(id).apply(event);
        } else {
            System.out.println("Unrecognised selection menu: " + id);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (pontoonChannelId != null && event.getChannel().getId().equals(pontoonChannelId)) {
            actionMap.get("pontoon").apply(event);
            return;
        }
    }
}
