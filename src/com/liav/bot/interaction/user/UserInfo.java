package com.liav.bot.interaction.user;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class UserInfo {
	private long money = 0;
	private long lastDaily = 0;
	private int level = 0;
	private int xp = 0;

	private final IUser user;

	public UserInfo(final IUser u) {
		user = u;
	}

	public UserInfo(final IUser u, final String data) {
		final String[] splitData = data.split(",");
		money = Long.parseLong(splitData[0]);
		lastDaily = Long.parseLong(splitData[1]);
		if (splitData.length >= 3) {
			level = Integer.parseInt(splitData[2]);
		}
		if (splitData.length >= 4) {
			xp = Integer.parseInt(splitData[3]);
		}

		user = u;
	}

	public int getXpUntilNextLevel() {
		return getXpForLevel(this.level);
	}
	
	public int getXpForLevel(int level){
		return (int) (100 + Math.pow(level * 2, 2));
	}

	public long getMoney() {
		return money;
	}

	public int getLevel() {
		return level;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}
	
	public int getTotalXp(){
		int total = xp;
		
		for(int i = 0;i<level;++i){
			total += getXpForLevel(i);
		}
		
		return total;
	}

	public void addXp(int xp, IChannel c) {
		this.xp += xp;

		if (this.xp > getXpUntilNextLevel()) {
			++level;
			setXp(0);
			addMoney(Configuration.LEVEL_UP_REWARD);

			EmbedBuilder e = new EmbedBuilder();
			e.withAuthorIcon(user.getAvatarURL());
			e.withAuthorName(user.getName());

			e.withThumbnail(user.getAvatarURL());

			e.withColor(100, 100, 255);
			e.withTimestamp(System.currentTimeMillis());
			e.withFooterText("Level up");
			e.withFooterIcon(Bot.getClient().getApplicationIconURL());

			e.withTitle("Level up!");
			e.withDesc("You are now level " + level + ".");

			e.appendField("Experience until next level", Integer.toString(getXpUntilNextLevel()), true);
			e.appendField("You have won $" + Configuration.LEVEL_UP_REWARD + "!", "Your new balance is $" + getMoney(),
					false);

			c.sendMessage(e.build());
		}
	}

	public void setMoney(final long mon) {
		money = mon;
	}

	public void addMoney(final long amountToAdd) {
		money += amountToAdd;
	}

	/**
	 * @return The time until daily can be used again, in milliseconds
	 */
	public long timeUntilNextDaily() {
		return (lastDaily + 86400000) - System.currentTimeMillis();
	}

	public boolean isDailyAvailable() {
		return System.currentTimeMillis() >= lastDaily + Configuration.DAILY_TIME;
	}

	public void doDaily() {
		addMoney(100);
		lastDaily = System.currentTimeMillis();
	}

	public String getData() {
		return Long.toString(money) + "," + Long.toString(lastDaily) + "," + Integer.toString(level) + ","
				+ Integer.toString(xp);
	}

	public IUser getUser() {
		return user;
	}
	
	public int getMoneyRank(final IGuild g){
		ArrayList<Entry<IUser, UserInfo>> moneyboard = Users.getMoneyboard(g);

		for (int i = 0; i < moneyboard.size(); ++i) {
			Entry<IUser, UserInfo> entry = moneyboard.get(i);
			if (entry.getKey().equals(user)) {
				return i + 1;
			}
		}
		
		return 9999;
	}
	
	public int getLevelRank(final IGuild g){
		ArrayList<Entry<IUser, UserInfo>> levelboard = Users.getLevelboard(g);

		for (int i = 0; i < levelboard.size(); ++i) {
			Entry<IUser, UserInfo> entry = levelboard.get(i);
			if (entry.getKey().equals(user)) {
				return i + 1;
			}
		}
		
		return 9999;
	}
}
