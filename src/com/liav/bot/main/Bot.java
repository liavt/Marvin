package com.liav.bot.main;

import java.security.SecureRandom;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.EventDispatcher;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.listeners.MentionListener;
import com.liav.bot.listeners.MessageListener;
import com.liav.bot.listeners.ReadyListener;
import com.liav.bot.main.tasks.TaskExecutor;

/**
 * Main class which controls the bot.
 * 
 * @author Liav
 *
 */
public class Bot {

	private static IDiscordClient client;
	/**
	 * {@link SecureRandom} intended for use in all psuedorandom number
	 * generation in the bot. It is here so there is only one instance of a
	 * {@link SecureRandom}.
	 */
	public static final SecureRandom random = new SecureRandom();

	/**
	 * Returns the {@link IDiscordClient} associated with the application, and
	 * logs it in.
	 * 
	 * @return The client, logged in with the OAuth2 token.
	 * @throws DiscordException
	 *             if an error occurs logging in
	 */
	public static IDiscordClient buildClient(String token)
	        throws DiscordException { // Returns
		// an
		// instance
		// of
		// the
		// discord
		// client
		final IDiscordClient client = new ClientBuilder().withToken(token)
		        .login();

		return client;
	}

	/**
	 * Retrieves this bot's {@link IDiscordClient}
	 * 
	 * @return the current session's client
	 */
	public static IDiscordClient getClient() {
		return client;
	}

	/**
	 * Overloads {@link #sendMessage(String, boolean, IChannel)}, with tts being
	 * false.
	 * 
	 * @param message
	 *            The message contents
	 * @param channel
	 *            The text channel to send the message on
	 * @throws MissingPermissionsException
	 *             If the bot doesn't have the required permissions to send
	 *             messages on the specified channel
	 * @throws DiscordException
	 *             Whether Discord has returned an error message.
	 * @throws HTTP429Exception
	 *             Whether there are too many requests sent to this channel.
	 * @see MessageBuilder
	 * @see IMessage
	 */
	public static void sendMessage(String message, IChannel channel)
	        throws HTTP429Exception, DiscordException,
	        MissingPermissionsException {
		sendMessage(message, false, channel);
	}

	/**
	 * Make the bot appear typing based on a boolean
	 * 
	 * @param t
	 *            Whether the bot should be typing
	 * @param c
	 *            Which channel the bot should be typing on.
	 */
	public static void setTyping(boolean t, IChannel c) {
		if (c.getTypingStatus() != t) {
			c.toggleTypingStatus();
		}
	}

	/**
	 * Sends a message with the bot's {@link IDiscordClient client} on a
	 * specified channel.
	 * 
	 * @param message
	 *            The message content. Can support markdown
	 * @param tts
	 *            Whether this message should be send with text-to-speech. Users
	 *            can disable this.
	 * @param channel
	 *            The text channel to send it on. The bot must have permissions
	 *            to post at this channel.
	 * @throws MissingPermissionsException
	 *             If the bot doesn't have the required permissions to send
	 *             messages on the specified channel
	 * @throws DiscordException
	 *             Whether Discord has returned an error message.
	 * @throws HTTP429Exception
	 *             Whether there are too many requests sent to this channel. * @see
	 *             MessageBuilder
	 * @see IMessage
	 */
	public static void
	        sendMessage(String message, boolean tts, IChannel channel)
	                throws HTTP429Exception, DiscordException,
	                MissingPermissionsException {
		setTyping(true, channel);
		if (!message.equals("") && !message.equals(" ")) {
			final MessageBuilder mb = new MessageBuilder(getClient())
			        .withChannel(channel).withContent(message);
			if (tts) {
				mb.withTTS();
			}
			mb.build();
		}
		setTyping(false, channel);
	}

	public static void reply(IMessage first, String message)
	        throws MissingPermissionsException, HTTP429Exception,
	        DiscordException {
		setTyping(true, first.getChannel());
		first.reply(message);
		setTyping(false, first.getChannel());
	}

	/**
	 * Get when the bot started
	 * 
	 * @return Unix time in milliseconds representing when the bot was launched
	 */
	public static long getStartTime() {
		return startTime;
	}

	/**
	 * Get the number of errors executed since the bot was started.
	 * 
	 * @return Errors encountered
	 */
	public static long getErrors() {
		return errors;
	}

	/**
	 * Get the number of {@linkplain Command commands} executed since the bot
	 * was started.
	 * 
	 * @return Commands executed
	 * @see CommandHandler
	 */
	public static long getCommands() {
		return commands;
	}

	/**
	 * Adds one error. Call this when an error occurs
	 */
	public static void incrementError() {
		errors++;
	}

	/**
	 * Adds one executed command for debug purposes. Should only be called by
	 * {@link CommandHandler}
	 */
	public static void incrementCommands() {
		commands++;
	}

	private static long startTime;
	private static long errors, commands;

	private static IChannel currentChannel = null;

	/**
	 * Get the channel the bot is currently speaking in. Used only by
	 * {@link MessageListener}
	 * 
	 * @return channel specified to speak in
	 */
	public static IChannel getCurrentChannel() {
		return currentChannel;
	}

	/**
	 * Set the channel for the bot to speak in. Used only by
	 * {@link MessageListener}
	 * 
	 * @param currentChannel
	 *            channel you want to bot to speak in
	 */
	public static void setCurrentChannel(IChannel currentChannel) {
		Bot.currentChannel = currentChannel;
	}

	public static void main(String[] args) throws DiscordException {

		System.out.println("Trying to build...");
		if (args.length != 1) {
			System.err.println("Must input a bot token!");
		} else {
			client = buildClient(args[0]);

			final EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new ReadyListener());
			dispatcher.registerListener(new MentionListener());
			dispatcher.registerListener(new MessageListener());
			System.out.println("Build complete.");
			final Executor e = Executors.newFixedThreadPool(1);
			e.execute(new TaskExecutor());
			startTime = System.currentTimeMillis();
		}
	}
}
