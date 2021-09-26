import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {
    private static Summoner summoner;

    public static void main(String[] args) throws LoginException {
        String token = System.getenv("TOKEN");

        JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("you"))
                .build();

        summoner = new Summoner();

        // This can take up to 1 hour to show up in the client
//        jda.upsertCommand("ping", "Calculate ping of the bot").queue();
        jda.upsertCommand("summon-heroes", "Performs a 10x summon").queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if (!event.getName().equals("summon-heroes")) return; // make sure we handle the right command

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("10x Summon Result");
        eb.setDescription(summoner.getSummonResult());

        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!ping"))
        {
            MessageChannel channel = event.getChannel();
            long time = System.currentTimeMillis();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Pong");
            eb.setDescription("HAHAHAHA");

            channel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
