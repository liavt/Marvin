package com.liav.bot.listeners;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.obj.IMessage;

import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.main.Bot;
import com.liav.bot.util.storage.CommandStorage;

/**
 * {@link IListener} which checks for when this bot is {@link MentionEvent
 * mentioned } After recieving the mention, checks if it is a {@link Command}
 * (as specified in {@link CommandStorage}), and
 * {@link CommandHandler#executeCommand(int, IMessage) executes it}.
 * 
 * @author Liav
 * @see IMessage
 * @see CommandHandler
 */
public class MentionListener implements IListener<MentionEvent> {

	@Override
	public void handle(MentionEvent event) {
		try {
			if (event.getMessage().getContent().startsWith("<")) {
				System.out.println("Mentioned.");
				final int offset = event.getMessage().getContent().indexOf(">") + 2;
				CommandHandler.executeCommand(offset, event.getMessage());
			}
		} catch (Throwable t) {
			Bot.incrementError();
			t.printStackTrace();
		}
	}
}
