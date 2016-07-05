package com.liav.bot.listeners;

import java.io.File;
import java.util.Optional;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.Image;

import com.liav.bot.main.Bot;

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
			System.out.println("Preparing...");
			final IDiscordClient client = event.getClient();
			client.updatePresence(false,
			        Optional.of("the important game of moderation"));
			// client.changeUsername("Marvin");
			client.changeAvatar(Image.forFile(new File("hal.png")));
			System.out.println("Waiting for connection...");
			while (!client.isReady()) {
				;
			}
			System.out.println("Connected and ready");

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
