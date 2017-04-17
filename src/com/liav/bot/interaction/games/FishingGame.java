package com.liav.bot.interaction.games;

import java.util.ArrayList;

import com.liav.bot.interaction.user.Users;
import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;
import com.liav.bot.util.AutomodUtil;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class FishingGame extends Game {
	private static final int HEIGHT = 5;
	private static final int WIDTH_STEP = 3;

	private int width;
	private int fishX = 0, fishY = 0;

	static class Boat {
		public IUser user;
		public int x = 0;
	}

	private ArrayList<Boat> boats = new ArrayList<Boat>();

	@Override
	public String getInitialMessage() {
		return "Starting fishing game...";
	}

	@Override
	public boolean tick() {
		String output = "Fishing game:\n";
		for (Boat b : boats) {
			for (int i = 0; i < (WIDTH_STEP / 2) - 1; ++i) {
				output += ":black_small_square:";
			}
			String name;
			if (b.user != null) {
				name = b.user.getName();
			} else {
				name = "BOT";
			}
			output += AutomodUtil.stringToEmoji(name.substring(0, 3));
			for (int i = 0; i < (WIDTH_STEP / 2) - 1; ++i) {
				output += ":black_small_square:";
			}
		}

		output += "\n";

		for (int j = 0; j < boats.size(); ++j) {
			for (int i = 0; i < (WIDTH_STEP / 2); ++i) {
				output += ":ocean:";
			}
			output += ":motorboat:";
			for (int i = 0; i < (WIDTH_STEP / 2); ++i) {
				output += ":ocean:";
			}
		}
		output += "\n";

		for (int y = HEIGHT - 1; y >= 0; --y) {
			for (int x = width - 1; x >= 0; --x) {
				if (x == fishX && y == fishY) {
					output += ":fish:";
				} else {
					output += ":large_blue_diamond:";
				}
			}
			output += "\n";
		}

		if (Bot.random.nextInt(10) > 3) {
			++fishY;
		} else if (Bot.random.nextBoolean()) {
			--fishY;
		}
		
		if(fishY < 0){
			fishY = 0;
		}

		if(Bot.random.nextBoolean()){
			--fishX;
		}else{
			++fishX;
		}

		if (fishX < 0) {
			fishX = 0;
		} else if (fishX >= width - 1) {
			fishX = width - 1;
		}
		
		Boat winner = null;

		if (fishY > HEIGHT) {
			for (int i = 1; i < boats.size() + 1; ++i) {
				if(fishX > (i-1) * WIDTH_STEP && fishX < i * WIDTH_STEP){
					winner = boats.get((boats.size() - 1) -(i - 1));
					if(winner.user == null){
						output += "Drats! The bot won!";
					}else{
						output += winner.user.getName() + " got the magic fish worth $"+Configuration.GAME_REWARD+"!";
						Users.getInfo(winner.user).addMoney(Configuration.GAME_REWARD);
					}
				}
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
		int x = WIDTH_STEP / 2;
		for (IUser u : users) {
			Boat b = new Boat();
			b.x = x;
			x += WIDTH_STEP;
			b.user = u;
			boats.add(b);
		}

		if (boats.size() == 1) {
			Boat b = new Boat();
			b.x = x;
			b.user = null;
			boats.add(b);
		}

		fishY = 0;

		width = WIDTH_STEP * boats.size();
		fishX = width / 2;
	}

}
