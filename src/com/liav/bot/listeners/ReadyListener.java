package com.liav.bot.listeners;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IRole;

import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;

/**
 * {@link IListener} which checks for {@link ReadyEvent ReadyEvents}. Sets the
 * bot's username, presence, and avatar.
 * 
 * @author Liav
 * @see Bot#getClient
 */
public class ReadyListener implements IListener<ReadyEvent> {

	@Override
	public void handle(ReadyEvent event) {
		try {
			System.out.println("Connected to Discord. Preparing...");
			Configuration.load();
			final IDiscordClient client = event.getClient();
			client.online(Configuration.properties.get("BOT_STATUS"));
			while (!client.isReady()) {
				Thread.sleep(1);
			}
			System.out.println("Connected and ready");
		} catch (Throwable e) {
			Bot.incrementError();
			e.printStackTrace();
		}
	}

}
