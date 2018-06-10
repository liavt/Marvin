package com.liav.bot.interaction.commands.interfaces;

import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.main.Bot;
import com.liav.bot.storage.CommandStorage;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Functional interface which is used in the constructor for {@link Command}.
 * Specifies what happens when the command is executed by the user.
 * 
 * @author Liav
 * @see StringCommand
 * @see CommandHandler
 * @see CommandStorage#commands
 */
@FunctionalInterface
public interface InteractiveCommand extends AdvancedCommand {
	/**
	 * Called when the user {@link Command#execute(String[], IMessage) executes
	 * a command} specified in {@link CommandStorage#commands}
	 * 
	 * @param param
	 *            Parameters for the command. If there are no parameters, will
	 *            have a {@code length} of 0.
	 * @param u
	 *            The {@link IUser} who executed the command
	 * @return The string for the bot to reply with.
	 * @see Bot#sendMessage
	 */
	String action(final String[] param, final IUser u);

	@Override
	default String action(final String[] param, final IMessage m) {
		return action(param, m.getAuthor());
	}
}
