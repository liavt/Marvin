package com.liav.bot.main;

import java.security.SecureRandom;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.listeners.ConsoleListener;
import com.liav.bot.listeners.MentionListener;
import com.liav.bot.listeners.MessageListener;
import com.liav.bot.listeners.ReadyListener;
import com.liav.bot.main.tasks.TaskExecutor;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

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

	private static long startTime;
	private static long errors, commands;
	private static boolean updated = false;

	private static IChannel currentChannel = null;

	/**
	 * Returns the {@link IDiscordClient} associated with the application, and
	 * logs it in.
	 * 
	 * @param token
	 *            Bot token for the Discord api
	 * 
	 * @return The client, logged in with the OAuth2 token.
	 * @throws DiscordException
	 *             if an error occurs logging in
	 */
	public static IDiscordClient buildClient(String token) throws DiscordException { // Returns
		// an
		// instance
		// of
		// the
		// discord
		// client
		final IDiscordClient client = new ClientBuilder().withToken(token).withRecommendedShardCount().login();

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
	 * @return The message that was sent
	 * @throws MissingPermissionsException
	 *             If the bot doesn't have the required permissions to send
	 *             messages on the specified channel
	 * @throws DiscordException
	 *             Whether Discord has returned an error message.
	 * @throws RateLimitException
	 *             Whether there are too many requests sent to this channel.
	 * @see MessageBuilder
	 * @see IMessage
	 */
	public static IMessage sendMessage(String message, IChannel channel)
			throws DiscordException, MissingPermissionsException, RateLimitException {
		return sendMessage(message, false, channel);
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
		c.setTypingStatus(t);
	}
	
	public static boolean wasUpdated(){
		return updated;
	}
	
	public static void setUpdated(boolean b){
		updated = b;
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
	 * @return The message that was sent
	 * @throws MissingPermissionsException
	 *             If the bot doesn't have the required permissions to send
	 *             messages on the specified channel
	 * @throws DiscordException
	 *             Whether Discord has returned an error message.
	 * @throws RateLimitException
	 * @see IMessage
	 */
	public static IMessage sendMessage(String message, boolean tts, IChannel channel)
			throws DiscordException, MissingPermissionsException {
		setTyping(true, channel);
		final MessageBuilder mb = new MessageBuilder(getClient()).withChannel(channel).withContent(message);
		if (tts) {
			mb.withTTS();
		}
		//retry sending
		try{
			return RequestBuffer.request(()->{
				setTyping(false, channel);
				return mb.build();
			}).get();
		}catch(RateLimitException e){
			Bot.incrementError();
			e.printStackTrace();
			//retry if failed for some reason
			return mb.build();
		}
	}
	
	public static void sendEmbed(EmbedBuilder e , IChannel c){
		RequestBuffer.request(()->{
			setTyping(true, c);
			c.sendMessage(e.build());
			setTyping(false, c);
		});
	}

	public static void reply(IMessage first, String message)
			throws MissingPermissionsException, DiscordException{
		RequestBuffer.request(()->{
			setTyping(true, first.getChannel());
			first.reply(message);
			setTyping(false, first.getChannel());
		});
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
		setUpdated(true);
		++errors;
	}

	/**
	 * Adds one executed command for debug purposes. Should only be called by
	 * {@link CommandHandler}
	 */
	public static void incrementCommands() {
		setUpdated(true);
		++commands;
	}

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

	public static void main(String[] args) throws Exception {
		System.out.println("Trying to build...");

		Configuration.init();

		if (!Configuration.properties.containsKey("DISCORD")) {
			throw new IllegalArgumentException("Must insert a Discord API key in "+Configuration.CONFIG_FILE+"!");
		}

		client = buildClient(Configuration.properties.get("DISCORD"));

		final EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new ReadyListener());
		dispatcher.registerListener(new MentionListener());
		dispatcher.registerListener(new MessageListener());
		final Executor e = Executors.newFixedThreadPool(3);
		e.execute(new TaskExecutor());
		e.execute(new ConsoleListener());
		e.execute(new ConfigurationSaver());

		System.out.println("Build complete.");
		startTime = System.currentTimeMillis();

	}
}
