package com.liav.bot.listeners;

import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.util.storage.CommandStorage;

import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * {@link IListener} which checks for when {@link MessageReceivedEvent messages
 * } are recieved on any channel this bot is on. After recieving the message,
 * checks if it is a {@link Command} (as specified in {@link CommandStorage}),
 * and {@link CommandHandler#executeCommand(int, IMessage) executes it}.
 * 
 * @author Liav
 * @see IMessage
 * @see CommandHandler
 */
public class MessageListener implements IListener<MessageReceivedEvent> {

	@Override
	public void handle(MessageReceivedEvent event) {
		try {
			String message = event.getMessage().getContent();
			System.out.println(event.getClient().getToken() + ": " + message);
			if (message.startsWith("!")) {
				CommandHandler.executeCommand(1, event.getMessage());
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
