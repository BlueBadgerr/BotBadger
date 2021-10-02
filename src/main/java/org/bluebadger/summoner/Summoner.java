package org.bluebadger.summoner;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Random;

public class Summoner {
    private static final String[] HERO_3_STAR = {"Plitvice", "Lapice", "Marina", "Arabelle", "Eva", "Bari", "Lupina",
            "Lahn", "Eugene", "Tinia", "Vishuvac", "Nari", "Bianca", "Oghma", "Alef", "Miya", "Future Princess",
            "Garam", "Beth", "Rue", "Gabriel", "Lynn", "Future Knight", "Veronica", "Noxia", "Mayreel", "Mk.99",
            "Lilith", "Lucy", "Sohee", "Yuze", "Eleanor", "Scintilla", "Erina", "Kamael", "Mk.2"};

    private static final String[] HERO_2_STAR = {"Eva", "Elvira", "White Beast", "Karina", "Loraine", "Lavi", "Favi",
            "Aoba", "Gremory", "Rachel", "Hekate", "Coco", "Marianne", "Sohee", "Mei", "Marvin", "Craig", "Akayuki",
            "Ranpang", "Yuze", "Aisha", "Shapira", "Dolf", "Amy", "Girgas", "Catherine", "Rie", "Neva"};

    private static final String[] HERO_1_STAR = {"Linda", "Bob", "Hyper", "Maria", "Lisa", "Leah", "Jay", "Dragon",
            "Blade", "Mina", "Hoshida", "Peggy", "Allie", "Oralie", "Kang", "Agatha", " DaVinci", "Kate", "Zoe", "Rio",
            "Nyan", "Marty Junior"};

    private static final int CHANCE_3_STAR = 2_750;
    private static final int CHANCE_2_STAR = 19_000;
    private static final int TOTAL_PROBABILITY = 100_000;

    private static final String DESCRIPTION_TEMPLATE = "Total summons: %d | Total white box: %d %n%s";

    private final Random random = new Random();

    public Summoner() {}

    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("summon-heroes")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("10x Summon Result");

            SummonResult summonResult = getSummonResult();
            eb.setDescription(String.format(DESCRIPTION_TEMPLATE,
                    10,
                    summonResult.totalWhiteBox,
                    summonResult.result));

            eb.setAuthor(event.getMember().getEffectiveName());
            event.replyEmbeds(eb.build()).addActionRow(Button.primary("reroll", "Roll again")).queue();
        }
    }

    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("reroll")) {
            MessageEmbed msg = event.getMessage().getEmbeds().get(0);

            if (!msg.getAuthor().getName().equals(event.getMember().getEffectiveName())) {
                event.reply("Can't do that. This is not your summon result!").setEphemeral(true).queue();
                return;
            }

            String[] extractedString = msg.getDescription().split("\\s+");

            SummonResult summonResult = getSummonResult();

            int previousTotal = Integer.parseInt(extractedString[2]);
            int previousWhiteBoxTotal = Integer.parseInt(extractedString[7]);
            int newTotal = previousTotal + 10;
            int newWhiteBoxTotal = previousWhiteBoxTotal + summonResult.totalWhiteBox;

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("10x Summon Result");
            eb.setDescription(String.format(DESCRIPTION_TEMPLATE,
                    newTotal,
                    newWhiteBoxTotal,
                    summonResult.result));
            eb.setAuthor(event.getMember().getEffectiveName());
            event.editMessageEmbeds(eb.build()).queue();
        }
    }

    private SummonResult getSummonResult() {
        StringBuilder sb = new StringBuilder();
        int totalWhiteBox = 0;

        for(int i = 0;  i < 9; i ++) {
            int result = random.nextInt(TOTAL_PROBABILITY);

            if (result < CHANCE_3_STAR) {
                sb.append(String.format("**\\*\\*\\* %s**%n", HERO_3_STAR[random.nextInt(HERO_3_STAR.length)]));
                totalWhiteBox++;
            } else if (result < CHANCE_3_STAR + CHANCE_2_STAR) {
                sb.append(String.format("\\*\\* %s%n", HERO_2_STAR[random.nextInt(HERO_2_STAR.length)]));
            } else {
                sb.append(String.format("\\* %s%n", HERO_1_STAR[random.nextInt(HERO_1_STAR.length)]));
            }
        }

        // Perform guarantee 2* minimum
        int result = random.nextInt(TOTAL_PROBABILITY);

        if (result < CHANCE_3_STAR) {
            sb.append(String.format("**\\*\\*\\* %s**%n", HERO_3_STAR[random.nextInt(HERO_3_STAR.length)]));
            totalWhiteBox++;
        } else {
            sb.append(String.format("\\*\\* %s%n", HERO_2_STAR[random.nextInt(HERO_2_STAR.length)]));
        }

        SummonResult summonResult = new SummonResult();
        summonResult.result = sb.toString();
        summonResult.totalWhiteBox = totalWhiteBox;

        return summonResult;
    }

    private static class SummonResult {
        protected String result;
        protected int totalWhiteBox;
    }
}
