import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {
    private static JDA jda;
    private static Summoner summoner;

    public static void main(String[] args) throws LoginException {
        String token = System.getenv("TOKEN");

        jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("you"))
                .build();

        // Bot action classes
        summoner = new Summoner();

        // This can take up to 1 hour to show up in the client
        jda.updateCommands()
                .addCommands(new CommandData("summon-heroes", "Performs a 10x summon"))
                .queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if (!event.getName().equals("summon-heroes")) return; // make sure we handle the right command

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("10x Summon Result");
        eb.setDescription(String.format("Total summons: 10%n%s", summoner.getSummonResult()));

        event.replyEmbeds(eb.build()).addActionRow(Button.primary("reroll", "Roll again")).queue();
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("reroll")) {
            int previousTotal = Integer.parseInt(event.getMessage().getEmbeds().get(0).getDescription().split("\\s+")[2]);
            int newTotal = previousTotal + 10;

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("10x Summon Result");
            eb.setDescription(String.format("Total summons: %d%n%s", newTotal, summoner.getSummonResult()));

            event.editMessageEmbeds(eb.build()).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // Do nothing for now
//        Message msg = event.getMessage();
//        if (msg.getContentRaw().equals("!ping"))
//        {
//            MessageChannel channel = event.getChannel();
//            long time = System.currentTimeMillis();
//
//            EmbedBuilder eb = new EmbedBuilder();
//            eb.setTitle("Pong");
//            eb.setDescription("HAHAHAHA");
//
//            channel.sendMessageEmbeds(eb.build()).queue();
//        }
    }
}
