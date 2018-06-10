package com.liav.bot.listeners;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.MessageBuilder;
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
	
	private static final long channels[] ={
			//200063366360727552L,
			292373089512194048L,
			292373141810970627L,
			305811054720712705L,
			292479347628179457L,
			297770643942801408L,
			298834045796876290L,
			294835363380199424L,
			311148812741640192L,
			293817089062469632L,
	};

	static void sendMessage(){
		final MessageBuilder mb = new MessageBuilder(Bot.getClient()).withChannel(Bot.getClient().getChannelByID(channels[Bot.random.nextInt(channels.length)])).withContent("I can easily name more people lea");
		mb.withTTS();
		mb.build();
		
		sendMessage();
	}
	
	@Override
	public void handle(ReadyEvent event) {
		try {
			System.out.println("Connected to Discord. Preparing...");
			Configuration.load();
			final IDiscordClient client = event.getClient();
			client.changePresence(StatusType.ONLINE, ActivityType.WATCHING, Configuration.properties.get("BOT_STATUS"));
			while (!client.isReady()) {
				Thread.sleep(1);
			}
			

			/*for(int i = 0;i<100;++i){
				try {
					RequestBuffer.request(ReadyListener::sendMessage);
				} catch (RateLimitException e) {
				}
			}*/
						
			System.out.println("Connected and ready");
		} catch (Throwable e) {
			Bot.incrementError();
			e.printStackTrace();
		}
	}

}
