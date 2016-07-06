package com.liav.bot.listeners;

import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.obj.IMessage;

import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.main.Bot;
import com.liav.bot.util.AutomodUtil;
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
			Bot.setTyping(true, event.getMessage().getChannel());
			if (event.getMessage().getContent().startsWith("<")) {
				System.out.println("Mentioned.");
				final int offset = event.getMessage().getContent().indexOf(">") + 2;
				final String message = event.getMessage().getContent()
						.substring(offset);
				if (message.startsWith(CommandHandler.getCommandPrefix())) {
					CommandHandler.executeCommand(offset, event.getMessage());
				} else {
					Bot.incrementCommands();
					Bot.reply(event.getMessage(),
							AutomodUtil.getCleverbotResponse(message));// respond
																		// with
																		// a
																		// clever
																		// response
				}
			}
		} catch (Throwable t) {
			Bot.incrementError();
			t.printStackTrace();
		}
	}
}
