package com.liav.bot.main;

import java.security.SecureRandom;
import java.util.concurrent.Executors;

import com.liav.bot.listeners.MentionListener;
import com.liav.bot.listeners.MessageListener;
import com.liav.bot.listeners.ReadyListener;
import com.liav.bot.main.tasks.TaskExecutor;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.EventDispatcher;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

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
	public static IDiscordClient buildClient() throws DiscordException { // Returns
		// an
		// instance
		// of
		// the
		// discord
		// client
		final IDiscordClient client = new ClientBuilder()
				.withToken("MTY4NzY2MjA2ODAxNzM5Nzc2.CewXzg.92a-omMSwsmZo0DzmMmOvMB3Pbs").login();

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
			throws HTTP429Exception, DiscordException, MissingPermissionsException {
		sendMessage(message, false, channel);
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
	 *             Whether there are too many requests sent to this channel.
	 *             * @see MessageBuilder
	 * @see IMessage
	 */
	public static void sendMessage(String message, boolean tts, IChannel channel)
			throws HTTP429Exception, DiscordException, MissingPermissionsException {
		if (!message.equals("") && !message.equals(" ")) {
			final MessageBuilder mb = new MessageBuilder(getClient()).withChannel(channel).withContent(message);
			if (tts) {
				mb.withTTS();
			}
			mb.build();
		}
	}

	public static void main(String[] args) throws DiscordException {
		client = buildClient();
		final EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new ReadyListener());
		dispatcher.registerListener(new MentionListener());
		dispatcher.registerListener(new MessageListener());
		Executors.newSingleThreadExecutor().execute(new TaskExecutor());
	}
}
