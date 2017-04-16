package com.liav.bot.interaction.user;

import com.liav.bot.main.Configuration;

public class UserInfo {
	private long money = 0;
	private long lastDaily = 0;
	
	public UserInfo(){}
	
	public UserInfo(final String data){
		final String[] splitData = data.split(",");
		money = Long.parseLong(splitData[0]);
		lastDaily = Long.parseLong(splitData[1]);
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(final long mon) {
		money = mon;
	}
	
	public void addMoney(final long amountToAdd){
		money += amountToAdd;
	}
	
	/**
	 * @return The time until daily can be used again, in milliseconds
	 * */
	public long timeUntilNextDaily(){
		return (lastDaily + 86400000) - System.currentTimeMillis();
	}
	
	public boolean isDailyAvailable(){
		return System.currentTimeMillis() >= lastDaily + Configuration.DAILY_TIME;
	}
	
	public void doDaily(){
		addMoney(100);
		lastDaily = System.currentTimeMillis();
	}
	
	public String getData(){
		return Long.toString(money) + "," + Long.toString(lastDaily);
	}
}
