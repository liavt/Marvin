package com.liav.bot.interaction.commands;

import com.liav.bot.interaction.commands.CategoryHandler.Category;
import com.liav.bot.interaction.commands.interfaces.AdvancedCommand;
import com.liav.bot.interaction.commands.interfaces.InteractiveCommand;
import com.liav.bot.interaction.commands.interfaces.StringCommand;
import com.liav.bot.storage.CommandStorage;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;

/**
 * Defines a text command for the bot to use. Commands can be send by mentioning
 * the bot or starting your message with !
 * <p>
 * All commands must have a help text to be used in the {@code help} command.
 * <p>
 * Commands will also reply to the person who executed it. If the command
 * replies with a TTS (text to speech) message, it will not @mention the user,
 * simply posting it to the {@link IChannel}.
 * <p>
 * All the commands are statically defined in {@link CommandStorage#commands}
 * for organizational sake.
 * 
 * @author Liav
 *
 * @see CommandHandler
 * @see AdvancedCommand
 */
public class Command {
	private final String name, help, category;
	private final AdvancedCommand action;
	private final boolean tts, admin;

	private final String[] aliases;
	
	/**
	 * Get whether only admin users can use this command
	 * 
	 * @return Admin status of command
	 */
	public boolean isAdminCommand() {
		return admin;
	}
	
	public String[] getAliases(){
		return aliases;
	}

	/**
	 * 
	 * Gets the command {@linkplain CategoryHandler.Category category's} name.
	 * 
	 * @return the category this command belongs to
	 * @see CategoryHandler
	 */
	public String getCategoryName() {
		return category;
	}

	/**
	 * Retrieves this command's {@link Category} from {@link CategoryHandler}
	 * 
	 * @return The category this command belongs to.
	 */
	public Category getCategory() {
		return CategoryHandler.getCategory(category);
	}

	/**
	 * Returns whether the command replies with a TTS (text-to-speech) message.
	 * If so, the bot will not @mention the user.
	 * 
	 * @return whether this returns a TTS message
	 * @see MessageBuilder
	 */
	public boolean isTTS() {
		return tts;
	}

	/**
	 * Returns the name of this command, used in ! or @mention. Additionally,
	 * this is what shows up in the {@code help} command.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;// the space is because there is always space when
					// mentioning
	}

	/**
	 * The {@link AdvancedCommand} which is executed when the command is run.
	 * <p>
	 * <b> It is highly recommended you use {@link #execute(String[], IMessage)}
	 * instead of this.</b>
	 * 
	 * @return the action
	 */
	public AdvancedCommand getAction() {
		return action;
	}

	/**
	 * Executes this command with the {@link AdvancedCommand} defined in the
	 * constructor.
	 * <p>
	 * If this {@link Command} uses a {@link StringCommand} instead, it will
	 * automatically overload it.
	 * 
	 * @param param
	 *            The parameters for the command
	 * @param m
	 *            The original message sent
	 * @return The {@code String} to reply to the user with.
	 */
	public String execute(String[] param, IMessage m) {
		return action.action(param, m);
	}

	/**
	 * Builds the {@link Command Command's} help text for use in the
	 * {@code help} command.
	 * 
	 * @return The help text as specified in the construcotr.
	 */
	public String getHelpText() {
		String output = help;
		if(tts){
			output += "\n*Sends a TTS message*";
		}
		
		if(aliases.length > 0){
			output += "\nAliases: **"+String.join("**, **",aliases) +"**";
		}
		return output;
	}

	/**
	 * Overloading constructor, and using a {@link InteractiveCommand} instead
	 * of an {@link AdvancedCommand}. {@link Command Commands} that are purely
	 * text based (the only thing it does is reply with a message) should use
	 * this constructor to save space.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param tts
	 *            Whether the response should utilize TTS (Text-To-Speech)
	 * @param admin
	 *            Whether this command can only be used by admin users
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link StringCommand#action(String[])} takes in the
	 *            command parameters.
	 */
	public Command(String n, String h, String cat, boolean tts, boolean admin, InteractiveCommand a) {
		this(n, h, cat, false, admin, (AdvancedCommand) a, "");// the cast because
		// otherwise the compiler
		// doesnt know what
		// constructor to choose
	}

	/**
	 * Overloading constructor, and using a {@link InteractiveCommand} instead
	 * of an {@link AdvancedCommand}. {@link Command Commands} that are purely
	 * text based (the only thing it does is reply with a message) should use
	 * this constructor to save space.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param tts
	 *            Whether the response should utilize TTS (Text-To-Speech)
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link StringCommand#action(String[])} takes in the
	 *            command parameters.
	 */
	public Command(String n, String h, String cat, boolean tts, InteractiveCommand a) {
		this(n, h, cat, false, (AdvancedCommand) a);// the cast because
													// otherwise the compiler
													// doesnt know what
													// constructor to choose
	}

	/**
	 * Overloading constructor, settings TTS to false, and using a
	 * {@link InteractiveCommand} instead of an {@link AdvancedCommand}.
	 * {@link Command Commands} that are purely text based (the only thing it
	 * does is reply with a message) should use this constructor to save space.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link StringCommand#action(String[])} takes in the
	 *            command parameters.
	 */
	public Command(String n, String h, String cat, InteractiveCommand a) {
		this(n, h, cat, false, a);
	}
	
	public Command(String n, String h, String cat, InteractiveCommand a, String aliases) {
		this(n, h, cat, false, a, aliases);
	}

	/**
	 * Overloading constructor, and using a {@link StringCommand} instead of an
	 * {@link AdvancedCommand}. {@link Command Commands} that are purely text
	 * based (the only thing it does is reply with a message) should use this
	 * constructor to save space.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param tts
	 *            Whether the response should utilize TTS (Text-To-Speech)
	 * @param admin
	 *            Whether this command can only be used by admin users
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link StringCommand#action(String[])} takes in the
	 *            command parameters.
	 */
	public Command(String n, String h, String cat, boolean tts, boolean admin, StringCommand a) {
		this(n, h, cat, tts, admin, (AdvancedCommand) a, "");
	}

	/**
	 * Overloading constructor, and using a {@link StringCommand} instead of an
	 * {@link AdvancedCommand}. {@link Command Commands} that are purely text
	 * based (the only thing it does is reply with a message) should use this
	 * constructor to save space.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param tts
	 *            Whether the response should utilize TTS (Text-To-Speech)
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link StringCommand#action(String[])} takes in the
	 *            command parameters.
	 */
	public Command(String n, String h, String cat, boolean tts, StringCommand a) {
		this(n, h, cat, tts, (AdvancedCommand) a);
	}

	/**
	 * Overloading constructor, settings TTS to false, and using a
	 * {@link StringCommand} instead of an {@link AdvancedCommand}.
	 * {@link Command Commands} that are purely text based (the only thing it
	 * does is reply with a message) should use this constructor to save space.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link StringCommand#action(String[])} takes in the
	 *            command parameters.
	 */
	public Command(String n, String h, String cat, StringCommand a) {
		this(n, h, cat, false, a);
	}

	/**
	 * Overloading constructor, settings TTS to false
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link AdvancedCommand#action(String[], IMessage)}
	 *            takes in the command parameters and the user who executed it.
	 * @see StringCommand
	 * @see StringCommand#getHelpCommand()
	 */
	public Command(String n, String h, String cat, AdvancedCommand a) {
		this(n, h, cat, false, a);
	}

	/**
	 * Default constructor.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param tts
	 *            Whether the command returns with a TTS (text-to-speech)
	 *            message. If this is true, the bot will not @mention.
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link AdvancedCommand#action(String[], IMessage)}
	 *            takes in the command parameters and the user who executed it.
	 *            {@link StringCommand} extends {@code AdvancedCommand,} so it
	 *            can be used in place here. However, there is also the
	 *            overloading constructor
	 *            {@link Command#Command(String, String, String,StringCommand)}
	 * @see StringCommand
	 * @see StringCommand#getHelpCommand()
	 */
	public Command(String n, String h, String cat, boolean tts, AdvancedCommand a, String aliases) {
		this(n, h, cat, tts, false, a, aliases);
	}
	
	public Command(String n, String h, String cat, boolean tts, AdvancedCommand a) {
		this(n, h, cat, tts, false, a, "");
	}

	/**
	 * Default constructor.
	 * 
	 * @param n
	 *            The name for the command. This is what the {@linkplain IUser}
	 *            types in to run the command.
	 * @param h
	 *            This is the help text that appears when the {@code help}
	 *            command is used.
	 * @param cat
	 *            Name of the category that this command is in.
	 * @param tts
	 *            Whether the command returns with a TTS (text-to-speech)
	 *            message. If this is true, the bot will not @mention.
	 * @param admin
	 *            Whether this command can only be used by admin users
	 * @param a
	 *            The functional interface that gets run when this command is
	 *            executed. {@link AdvancedCommand#action(String[], IMessage)}
	 *            takes in the command parameters and the user who executed it.
	 *            {@link StringCommand} extends {@code AdvancedCommand,} so it
	 *            can be used in place here. However, there is also the
	 *            overloading constructor
	 *            {@link Command#Command(String, String,String, StringCommand)}
	 * @see StringCommand
	 * @see StringCommand#getHelpCommand()
	 */
	public Command(String n, String h, String cat, boolean tts, boolean admin, AdvancedCommand a, String aliases) {
		this.name = n;
		this.action = a;
		this.help = h;
		this.tts = tts;
		this.category = cat;
		this.admin = admin;
		this.aliases = aliases.split(",");
	}

	public Command(String n, String h, String cat, StringCommand a, String aliases) {
		this(n,h,cat,false,false,a,aliases);
	}
	
	public Command(String n, String h, String cat, AdvancedCommand a, String aliases) {
		this(n,h,cat,false,false,a,aliases);
	}
}
