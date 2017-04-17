package com.liav.bot.interaction.games;

import java.util.ArrayList;

import com.liav.bot.interaction.user.Users;
import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;

import sx.blah.discord.handle.obj.IMessage;
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
	public String getInitialMessage() {
		return "Starting to race bunnies...";
	}

	@Override
	public boolean tick() {
		String output = "Bunny racing:\n";
		
		Bunny winner = null;
		for(Bunny b : bunnies){
			b.tick();
			for(int i = 0;i<10;++i){
				if(i < b.progress){
					output += ":black_small_square:";
				}else if(i == b.progress){
					output += ":rabbit: ";
				}else{
					output += ":white_small_square:";
				}
			}
			output += ":fishing_pole_and_fish: ";
			if(b.user != null){
				output += b.user.getName();
			}else{
				output += ":rabbit2:";
			}
			output += "\n";
			if(b.progress >= 10){
				winner = b;
			}
		}
		
		if(winner != null){
			if(winner.user == null){
				output += "Oh no! The evil bunny won!";
			}else{
				output += "We have a winner! "+winner.user.getName()+" has won $"+Configuration.GAME_REWARD +"!";
				Users.getInfo(winner.user).addMoney(Configuration.GAME_REWARD);
			}
		}
		
		try {
			message.edit(output);
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			e.printStackTrace();
			Bot.incrementError();
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

}
