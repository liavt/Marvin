package com.liav.bot.interaction.games;

import java.util.ArrayList;

import com.liav.bot.main.Bot;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class BunnyGame extends Game {
	static class Bunny {
		public IUser user = null;
		public int progress = 0;

		public void tick() {
			int value = Bot.random.nextInt(10);
			if(value >= 8){
				progress += 2;
			}else if (value >= 7) {
				++progress;
			}else if(value <= 0 && progress > 0){
				--progress;
			}
		}
	}

	private ArrayList<Bunny> bunnies = new ArrayList<>();

	public BunnyGame() {
		super();
	}

	@Override
	public boolean tick() {
		String output = "Bunny racing:\n";
		
		Bunny winner = null;
		for(Bunny b : bunnies){
			b.tick();
			for(int i = 0;i<10;++i){
				if(i < b.progress){
					output += "▪️";
				}else if(i == b.progress){
					output += "🐰";
				}else{
					output += "▫️";
				}
			}
			output += "🎣 ";
			if(b.user != null){
				output += b.user.getName();
			}else{
				output += "🐇";
			}
			output += "\n";
			if(b.progress >= 10){
				winner = b;
			}
		}
		
		if(winner != null){
			win(winner.user, message.getChannel());
		}
		
		try {
			message.edit(output);
		} catch (MissingPermissionsException | DiscordException e) {
			e.printStackTrace();
			Bot.incrementError();
		} catch(RateLimitException e){
			//do nothing
		}
		
		return winner != null;
	}

	@Override
	public void init() {
		for(IUser u : users){
			Bunny b = new Bunny();
			b.user = u;
			bunnies.add(b);
		}
		if(bunnies.size() == 1){
			Bunny b = new Bunny();
			b.user = null;
			bunnies.add(b);
		}
	}

	@Override
	public String getName() {
		return "Bunny Racing";
	}

}
