package com.liav.bot.interaction.games;

import java.util.ArrayList;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class Game {
	private long timeUntilStart = 0;
	protected IMessage message = null;
	protected ArrayList<IUser> users = new ArrayList<>();
	
	public Game(){
	}
		
	public abstract boolean tick();
	
	public abstract void init();
	
	public void deductTime(long time){
		timeUntilStart -= time;
	}
	
	public boolean hasStarted(){
		return timeUntilStart <= 0;
	}
	
	public void setTimeUntilStart(final long time){
		timeUntilStart = time;
	}
	
	public long getTimeUntilStart(){
		return timeUntilStart;
	}
	
	public void setMessage(final IMessage m){
		message = m;
	}
	
	public IMessage getMessage(){
		return message;
	}
	
	public ArrayList<IUser> getUsers(){
		return users;
	}
	
	public void addUser(IUser u){
		users.add(u);
	}
}
