package org.gruve;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.gruve.commands.ServerCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class Listeners extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Guild guild = event.getJDA().getGuildById(921820956769521735L);

        assert guild != null;
        guild.upsertCommand("tribes", "Does stuff related to the tribes server")
                .addSubcommands(
                new SubcommandData("stats", "Prints the stats of each member in the tribe")
        ).queue();
        guild.upsertCommand("runcommand", "Runs a command").addOption(OptionType.STRING, "command", "a command to send to the minecraft server").queue();
        guild.upsertCommand("open", "Opens a server").queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent e) {
        if (e.getButton().getId().contains("open")) {
            String serverID = e.getButton().getId().split("-")[1];
            String result = ServerCommand.startServer(serverID);
            System.out.println("stuff");
            Message message = e.getMessage();
            if (result.startsWith("/1")) { // message that will be tracked
                result = result.substring(2);
                Main.lastStatusMessageID = Long.parseLong(message.getId());
                Main.lastStatusMessageChannelID = Long.parseLong(message.getChannelId());
                Main.saveServerStatusMessageIDs();
                Main.setServerStatus(ServerStatus.LOADING);
            }
            MessageEditData editData = MessageEditBuilder.from(MessageEditData.fromMessage(message))
                    .setComponents(Collections.emptyList()) // removes the buttons
                    .setContent(result) // updates so it says successfully opened the server
                    .build();
            message.editMessage(editData).queue();
            e.reply("Have fun on the server :D").setEphemeral(true).queue();
        }
    }
}