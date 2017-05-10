package com.liav.bot.listeners;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.main.AutomodUtil;
import com.liav.bot.main.Bot;
import com.liav.bot.storage.CommandStorage;

/**
 * {@link IListener} which checks for when {@link MessageReceivedEvent messages
 * * } are recieved on any channel this bot is on. After recieving the message,
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
			// this is to let the "owner" of the bot to run it
			if (event.getMessage().getChannel().isPrivate()
					&& AutomodUtil.isAdmin(event.getMessage().getAuthor(), event.getMessage().getGuild())) {
				Bot.sendMessage(CommandHandler.handleDM(event.getMessage().getContent()),event.getMessage().getChannel());
			} else if (message.startsWith(CommandHandler.getCommandPrefix())) {
				CommandHandler.executeCommand(CommandHandler.getCommandPrefix().length(), event.getMessage());
			}
		} catch (Throwable t) {
			Bot.incrementError();
			t.printStackTrace();
		}
	}
}
