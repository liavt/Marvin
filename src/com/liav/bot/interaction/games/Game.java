package com.liav.bot.interaction.games;

import java.util.ArrayList;

import com.liav.bot.interaction.user.UserInfo;
import com.liav.bot.interaction.user.Users;
import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public abstract class Game {
	private long timeUntilStart = 0;
	protected IMessage message = null;
	protected ArrayList<IUser> users = new ArrayList<>();

	public Game() {
	}

	public abstract boolean tick();

	public abstract void init();

	public abstract String getName();

	public void deductTime(long time) {
		timeUntilStart -= time;
	}

	public void win(IUser winner, IChannel c) {
		if (winner == null) {
			c.sendMessage("Drats! The bot won!");
		} else {
			UserInfo info = Users.getInfo(winner);
			info.addXp(Configuration.GAME_WIN_XP, c);
			info.addMoney(Configuration.GAME_REWARD);
			
			EmbedBuilder e = new EmbedBuilder();
			e.withAuthorIcon(winner.getAvatarURL());
			e.withThumbnail(winner.getAvatarURL());
			e.withAuthorName("Congratualations!");
			e.appendField(winner.getName() + " has won $" + Configuration.GAME_REWARD + " from " + getName() + "!",
					winner.getName() + " now has $" + Users.getInfo(winner).getMoney() +"!", true);
			e.withColor(0, 255, 0);
			e.withTimestamp(System.currentTimeMillis());
			e.withFooterIcon(Bot.getClient().getApplicationIconURL());
			e.withFooterText(getName());

			c.sendMessage(e.build());
		}
	}

	public boolean hasStarted() {
		return timeUntilStart <= 0;
	}

	public void setTimeUntilStart(final long time) {
		timeUntilStart = time;
	}

	public long getTimeUntilStart() {
		return timeUntilStart;
	}

	public void setMessage(final IMessage m) {
		message = m;
	}

	public IMessage getMessage() {
		return message;
	}

	public ArrayList<IUser> getUsers() {
		return users;
	}

	public void addUser(IUser u) {
		users.add(u);
	}
}
