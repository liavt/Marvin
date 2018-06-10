package com.liav.bot.interaction.commands.interfaces;

import com.liav.bot.interaction.commands.CategoryHandler;
import com.liav.bot.interaction.commands.CategoryHandler.Category;
import com.liav.bot.main.AutomodUtil;
import com.liav.bot.storage.CommandStorage;
import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Optional interface of {@link InteractiveCommand} which doesn't require the
 * {@link IUser} parameter to save space. Is overloaded in
 * {@link Command#Command(String,String,String,StringCommand)}
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
	 * {@link Command#Command(String, String,String, StringCommand)}.
	 * <p>
	 * Regardless of what type of {@link InteractiveCommand} you specify in
	 * {@link Command#Command(String, String, String,boolean,boolean, AdvancedCommand)}
	 * , {@link Command#execute(String[], IMessage)} will always work and
	 * overload everything. Using this {@code interface} requires no extra steps
	 * to execute the {@link Command}
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
		return (String[] p, IMessage m) -> {
			if (p.length <= 0 || p[0] == null) {
				final boolean admin = AutomodUtil.isAdmin(m.getAuthor(), m.getChannel().getGuild());
				final StringBuilder sb = new StringBuilder("Available Commands: \n");
				for (Category cat : CategoryHandler.getCategories()) {
					int iteration = 0;
					sb.append("\n__Category ").append(cat.getName()).append("__\n");
					for (Command c : cat.getCommands()) {
						if (!c.isAdminCommand()||(c.isAdminCommand()&&admin)) {
							sb.append("**").append(c.getName());
							if(c.isAdminCommand()&&admin){
								sb.append("(admin)");
							}
							sb.append("**\t");
							iteration++;
							if (iteration % 8 == 0) {
								sb.append('\n');
							}
						}
					}
					sb.append("\n");
				}
				return sb.append("\nUse `")
						.append(CommandHandler.getCommandPrefix())
						.append("help [command]` to learn more about a specific command.\n\nThere are `")
						.append(CommandStorage.commands.length)
						.append("` commands in `")
						.append(CategoryHandler.getCategories().length)
						.append("` command categories.\nUse `")
						.append(CommandHandler.getCommandPrefix())
						.append("category` to view them in more detail").toString();
			}
			final Command c = CommandHandler.getCommand(p[0]);
			if (c == null) {
				return p[0] + " is not a valid command.";
			}
			return c.getHelpText() + (c.isAdminCommand() ? "\n*This is an admin-only command*" : "");
		};
	}
}
