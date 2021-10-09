package org.bluebadger.interfaces;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Action {
    void apply(SlashCommandEvent event);
    void apply(ButtonClickEvent event);
    void apply(SelectionMenuEvent event);
    void apply(MessageReceivedEvent event);
}
