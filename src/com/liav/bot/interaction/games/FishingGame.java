package com.liav.bot.interaction.games;

import java.util.ArrayList;

import com.liav.bot.main.AutomodUtil;
import com.liav.bot.main.Bot;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class FishingGame extends Game {
	private static final int HEIGHT = 10;

	private int widthStep;
	private int width;
	private int fishX = 0, fishY = 0;

	static class Boat {
		public IUser user;
		public int x = 0;
	}

	private ArrayList<Boat> boats = new ArrayList<Boat>();

	@Override
	public boolean tick() {
		String output = "Fishing game:\n";
		boolean isWhite = false;
		for (Boat b : boats) {
			for (int i = 0; i < (widthStep / 2) - 1; ++i) {
				if (isWhite) {
					output += "▫️";
				} else {
					output += "▪️";
				}
			}
			String name;
			if (b.user != null) {
				name = b.user.getName();
			} else {
				name = "BOT";
			}
			output += AutomodUtil.stringToEmoji(name.substring(0, 3));
			for (int i = 0; i < (widthStep / 2) - 1; ++i) {
				if (isWhite) {
					output += "▫️";
				} else {
					output += "▪️";
				}
			}
			isWhite = !isWhite;
		}

		output += "\n";

		for (int j = 0; j < boats.size(); ++j) {
			for (int i = 0; i < (widthStep / 2); ++i) {
				output += "🌊";
			}
			if(isWhite){
				output += "🛥️";
			}else{
				output += "⛵";
			}
			for (int i = 0; i < (widthStep / 2); ++i) {
				output += "🌊";
			}
			isWhite = !isWhite;
		}
		output += "\n";

		for (int y = HEIGHT - 1; y >= 0; --y) {
			for (int x = width - 1; x >= 0; --x) {
				if (x == fishX && y == fishY) {
					output += "🐠";
				} else {
					output += "🔷";
				}
			}
			output += "\n";
		}

		if (Bot.random.nextInt(10) > 3) {
			++fishY;
		} else if (Bot.random.nextBoolean()) {
			--fishY;
		}

		if (fishY < 0) {
			fishY = 0;
		}

		if (Bot.random.nextBoolean()) {
			--fishX;
		} else {
			++fishX;
		}

		if (fishX < 0) {
			fishX = 0;
		} else if (fishX >= width - 1) {
			fishX = width - 1;
		}

		IUser winner = null;

		if (fishY >= HEIGHT) {
			for (int i = 1; i < boats.size() + 1; ++i) {
				if (fishX > (i - 1) * widthStep && fishX < i * widthStep) {
					winner = boats.get((boats.size() - 1) - (i - 1)).user;
					win(winner, message.getChannel());
				}
			}
			if (winner == null) {
				fishY = HEIGHT - 1;
			}
		}

		try {
			message.edit(output);
		} catch (MissingPermissionsException | DiscordException e) {
			e.printStackTrace();
			Bot.incrementError();
		} catch (RateLimitException e) {
			// do nothing
		}

		return winner != null;
	}

	@Override
	public void init() {
		if (users.size() <= 2) {
			widthStep = 9;
		} else if (users.size() == 3) {
			widthStep = 8;
		} else if (users.size() == 4) {
			widthStep = 7;
		} else if (users.size() == 5) {
			widthStep = 6;
		} else {
			widthStep = 5;
		}

		int x = widthStep / 2;
		for (IUser u : users) {
			Boat b = new Boat();
			b.x = x;
			x += widthStep;
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

		width = ((widthStep) * boats.size());
		fishX = width / 2;
	}

	@Override
	public String getName() {
		return "Fishing Game";
	}

}
