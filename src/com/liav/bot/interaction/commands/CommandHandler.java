package com.liav.bot.interaction.commands;

import sx.blah.discord.handle.obj.IMessage;

import com.liav.bot.main.Bot;
import com.liav.bot.util.storage.CommandStorage;

/**
 * Static class which handles all {@link Command commands} for the bot.
 * 
 * @author Liav
 * @see CommandStorage#commands
 */
public final class CommandHandler {
	private CommandHandler() {
	}

	/**
	 * Finds the specified {@link Command} in {@link CommandStorage#commands}
	 * 
	 * @param s
	 *            The name of the {@code Command} to find.
	 * @return The {@code Command} with the {@linkplain Command#getName() name}
	 *         of {@code s.} Returns null if no {@code Command} was found.
	 */
	public static Command getCommand(String s) {
		final Command[] commands = CommandStorage.commands;
		for (Command c : commands) {
			if (c.getName().equalsIgnoreCase(s)
			        || (" " + c.getName()).equalsIgnoreCase(s)) { return c; }
		}
		return null;
	}

	/**
	 * Overloads {@link #executeCommand(int, IMessage)} with an offset of 0.
	 * 
	 * @param m
	 *            The IMessage to be queried for a {@link Command}
	 */
	public static void executeCommand(IMessage m) {

		executeCommand(0, m);
	}

	/**
	 * Checks a {@link IMessage message} for a {@link Command}, and if one is
	 * found,
	 * {@linkplain Command#execute(String[], sx.blah.discord.handle.obj.IUser)
	 * executes it}.
	 * <p>
	 * The list of {@code Commands} this pulls from is defined in
	 * {@link CommandStorage#commands}
	 * 
	 * @param offset
	 *            Offset of the command in the {@code String}. Calls
	 *            {@link String#substring(int)} with this parameter.
	 * @param m
	 *            The message sent to the bot with the specified command.
	 */
	public static void executeCommand(int offset, IMessage m) {
		try {
			m.getChannel().toggleTypingStatus();
			Bot.incrementCommands();
			final String command = m.getContent();
			final String[] split = command.substring(offset).split(" ");
			String[] param = new String[split.length > 1 ? split.length - 1 : 0];
			if (split.length > 1) {
				System.arraycopy(split, 1, param, 0, param.length);
			}
			final Command c = getCommand(split[0]);
			if (c == null) { return; }
			System.out.println("Executing command: " + split[0]);
			final String reply = c
			        .execute(param, m.getAuthor(), m.getChannel());
			if (!c.isTTS()) {
				m.reply(reply);
			} else {
				Bot.sendMessage(reply, c.isTTS(), m.getChannel());
			}
			m.getChannel().toggleTypingStatus();
		} catch (Throwable t) {
			t.printStackTrace();
			Bot.incrementError();
		}
	}
}
