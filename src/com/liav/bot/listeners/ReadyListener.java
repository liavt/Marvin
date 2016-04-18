package com.liav.bot.listeners;

import java.io.File;
import java.util.Optional;

import com.liav.bot.main.Bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.Image;

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
			final IDiscordClient client = event.getClient();
			client.updatePresence(false, Optional.of("in moderation"));
			client.changeUsername("Automoderator");
			client.changeAvatar(Image.forFile(new File("hal.png")));
			if (client.isReady()) {
				System.out.println("Connected and ready");
			}
		} catch (DiscordException | HTTP429Exception e) {
			e.printStackTrace();
		}
	}

}
