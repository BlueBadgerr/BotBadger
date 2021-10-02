package org.bluebadger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bluebadger.blackjack.Blackjack;
import org.bluebadger.libraries.Database;
import org.bluebadger.summoner.Summoner;

import javax.security.auth.login.LoginException;
import java.sql.*;

public class Bot extends ListenerAdapter {
    private static Summoner summoner;
    private static Blackjack blackjack;

    public static void main(String[] args) throws LoginException, SQLException {
        String token = System.getenv("TOKEN");

        JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("you"))
                .build();

        // Connect to Postgres database
        Database.connect(System.getenv("JDBC_DATABASE_URL"));

        // Bot action classes
        summoner = new Summoner();
        blackjack = new Blackjack();

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
            case "blackjack":
                // do nothing for now
                break;
            default:
                System.out.printf("Unknown slash command %s%n", event.getName());
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        switch (event.getComponentId()) {
            case "summoner-reroll":
                summoner.onButtonClick(event);
                break;
            default:
                System.out.printf("Unknown button press %s%n", event.getComponentId());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();

        if (msg.getContentRaw().equals("add")) {
            blackjack.add(msg.getMember().getUser().getId());
        }

        if (msg.getContentRaw().equals("get")) {
            channel.sendMessage(blackjack.get(msg.getMember().getUser().getId()).toString()).queue();
        }
    }
}
