package com.liav.bot.interaction.commands.interfaces;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import com.liav.bot.interaction.commands.CategoryHandler;
import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.util.AutomodUtil;
import com.liav.bot.util.storage.CommandStorage;

/**
 * Optional interface of {@link InteractiveCommand} which doesn't require the
 * {@link IUser} parameter to save space. Is overloaded in
 * {@link Command#Command(String,String,StringCommand)}
 * <p>
 * Intended to be used for commands which are entirely text based, which don't
 * require the executing user.
 * 
 * @author Liav
 * @see CommandStorage#commands
 *
 */
@FunctionalInterface
public interface StringCommand extends InteractiveCommand {

	/**
	 * Overloads {@link InteractiveCommand#action(String[], IUser)} for
	 * simplicity's sake. Can be used in
	 * {@link Command#Command(String, String, StringCommand)}.
	 * <p>
	 * Regardless of what type of {@link InteractiveCommand} you specify in
	 * {@link Command#Command(String, String, boolean, InteractiveCommand)},
	 * {@link Command#execute(String[], IUser)} will always work and overload
	 * everything. Using this {@code interface} requires no extra steps to
	 * execute the {@link Command}
	 * 
	 * @param param
	 *            The parameters of the command
	 * @return The message for the bot to respond with.
	 */
	String action(String[] param);

	@Override
	default String action(String[] param, IUser u) {
		return action(param);
	}

	/**
	 * Creates a {@link StringCommand} for use by the {@link Command help
	 * command}. Requires {@link CommandStorage#commands} to be defined first,
	 * so that is why it is here.
	 * 
	 * @return a {@code StringCommand} with the code for the {@code help}
	 *         command.
	 */
	public static AdvancedCommand getHelpCommand() {
		return (String[] p, IUser u, IChannel ch) -> {
			if (p.length <= 0 || p[0] == null) {
				final boolean admin = AutomodUtil.isAdmin(u, ch.getGuild());
				final StringBuilder sb = new StringBuilder(
				        "Available Commands: \n");
				int iteration = 0;
				for (Command c : CommandStorage.commands) {
					if ((!c.isAdminCommand())) {
						sb.append("**").append(c.getName()).append("**	");
						iteration++;
						if (iteration % 8 == 0) {
							sb.append('\n');
						}
					}
				}
				if (admin) {
					sb.append("\nAdmin commands:");
					for (Command c : CommandStorage.commands) {
						if ((c.isAdminCommand())) {
							sb.append("**").append(c.getName()).append("**	");
							iteration++;
							if (iteration % 8 == 0) {
								sb.append('\n');
							}
						}
					}
				}
				return sb
				        .append("\nUse *help [command]* to learn more about a specific command.\n\nThere are ")
				        .append(CategoryHandler.getCategories().length)
				        .append(" command categories.\nUse *category* to view them in more detail")
				        .toString();
			}
			final Command c = CommandHandler.getCommand(p[0]);
			if (c == null) { return c + " is not a valid command."; }
			return c.getHelpText();
		};
	}
}
