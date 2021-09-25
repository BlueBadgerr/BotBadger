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
    public static void main(String[] args) throws LoginException {
        String token = System.getenv("TOKEN");

        JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("you"))
                .build();

        // This can take up to 1 hour to show up in the client
        jda.upsertCommand("ping", "Calculate ping of the bot").queue();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        if (!event.getName().equals("ping")) return; // make sure we handle the right command
        long time = System.currentTimeMillis();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Pong");

        event.replyEmbeds(eb.build()).queue(response -> {
            EmbedBuilder eb2 = new EmbedBuilder();
            eb2.setTitle("Pong");
            eb2.setDescription(String.format("Time taken %d ms", System.currentTimeMillis() - time));
            response.editOriginalEmbeds(eb2.build()).queue();
        });
//        event.reply("Pong!").setEphemeral(false) // reply or acknowledge
//                .flatMap(v ->
//                        event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
//                ).queue(); // Queue both reply and edit
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
