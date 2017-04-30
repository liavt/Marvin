package com.liav.bot.interaction.commands;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.liav.bot.interaction.user.UserInfo;
import com.liav.bot.interaction.user.Users;
import com.liav.bot.main.AutomodUtil;
import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;
import com.liav.bot.util.storage.CommandStorage;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

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
	 * Used to execute tasks
	 */
	private static final ExecutorService exe = Executors.newCachedThreadPool();

	/**
	 * Return the {@link String} that is used in commands.
	 * <p>
	 * For example, if this method returns {@code !,} then commands are made via
	 * {@code ![name]}
	 * 
	 * @return Prefix of commands
	 */
	public static final String getCommandPrefix() {
		return Configuration.properties.get("COMMAND_PREFIX");
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
		if (s.isEmpty()) {
			return null;
		}

		final Command[] commands = CommandStorage.commands;
		for (Command c : commands) {
			if (c.getName().equalsIgnoreCase(s) || (" " + c.getName()).equalsIgnoreCase(s)) {
				return c;
			}
			for (String al : c.getAliases()) {
				if (!al.isEmpty() && (al.equalsIgnoreCase(s) || (" " + al).equalsIgnoreCase(s))) {
					return c;
				}
			}
		}
		return null;
	}

	public static String handleDM(String message) throws IOException {
		if (message.startsWith(getCommandPrefix() + "set")) {
			final String[] param = message.split(" ");
			if (param.length != 2) {
				return "Invalid!";
			}
			Bot.setCurrentChannel(Bot.getClient().getChannelByID(Long.parseLong(param[1])));
			return "Set speaking channel to " + param[1] + " ("
					+ Bot.getClient().getChannelByID(Long.parseLong(param[1])).getName() + ")";
		} else if (message.startsWith(getCommandPrefix() + "save")) {
			Configuration.save();
			return "Saved.";
		}
		if (Bot.getCurrentChannel() == null) {
			return "Channel not set!";
		}
		try {
			Bot.sendMessage(message, Bot.getCurrentChannel());
		} catch (DiscordException | MissingPermissionsException | RateLimitException e) {
			Bot.incrementError();
			e.printStackTrace();
		}
		return "Sent message: " + message;
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
	 * {@linkplain Command#execute(String[], sx.blah.discord.handle.obj.IMessage)
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
		final Thread thread = new Thread(() -> {
			try {
				if (!m.getAuthor().isBot()) {
					UserInfo info = Users.getInfo(m.getAuthor());
					info.addXp(Configuration.COMMAND_XP, m.getChannel());

					String command = m.getContent().substring(offset);

					while (command.startsWith(" ")) {
						command = command.substring(1);
					}

					final String[] split = command.split(" ");
					String[] param = new String[split.length > 1 ? split.length - 1 : 0];
					if (split.length > 1) {
						System.arraycopy(split, 1, param, 0, param.length);
					}

					final Command c = getCommand(split[0]);
					if (c == null) {
						return;
					}

					if ((c.isAdminCommand() && AutomodUtil.isAdmin(m.getAuthor(), m.getChannel().getGuild()))
							|| !c.isAdminCommand()) {
						Bot.incrementCommands();
						System.out.println("Executing command: " + split[0]);
						// zero width space prevents it from accidently
						// executing other bots
						final String reply = c.execute(param, m);
						if (reply != null && !reply.equals("") && !reply.isEmpty()) {
							Bot.sendMessage("\u200B" + reply, c.isTTS(), m.getChannel());
						}
					} else {
						Bot.sendMessage("Must be an admin to use this command!", false, m.getChannel());
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
				Bot.incrementError();
			}
		});
		thread.setDaemon(true);
		exe.execute(thread);
	}
}
