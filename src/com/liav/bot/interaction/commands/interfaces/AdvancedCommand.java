package com.liav.bot.interaction.commands.interfaces;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import com.liav.bot.interaction.commands.Command;
import com.liav.bot.main.Bot;
import com.liav.bot.util.storage.CommandStorage;

@FunctionalInterface
public interface AdvancedCommand {
	/**
	 * Called when the user {@link Command#execute(String[], IUser) executes a
	 * command} specified in {@link CommandStorage#commands}
	 * 
	 * @param param
	 *            Parameters for the command. If there are no parameters, will
	 *            have a {@code length} of 0.
	 * @param u
	 *            The {@link IUser} who executed the command
	 * @param c
	 *            The {@link IChannel} the command was done in
	 * @return The string for the bot to reply with.
	 * @see Bot#sendMessage
	 */
	String action(final String[] param, final IUser u, final IChannel c);
}
