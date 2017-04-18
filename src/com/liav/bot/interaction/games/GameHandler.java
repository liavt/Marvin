package com.liav.bot.interaction.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;
import com.liav.bot.main.tasks.TaskPool;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class GameHandler {
	private static Map<IChannel, Game> games = new HashMap<>();
	
	public static Map<IChannel, Game> getGames(){
		return games;
	}
	
	public static boolean hasGame(final IChannel c){
		return games.containsKey(c);
	}
	
	public static Game getGame(final IChannel c){
		return games.get(c);
	}
	
	public static void addGame(final Game g, final IChannel c){
		games.put(c, g);
		TaskPool.addTask(()->{
			if(!g.hasStarted()){
				g.deductTime(Configuration.UPDATE_RATE);
				if(g.getTimeUntilStart() <= 0){
					try {
						g.setMessage(Bot.sendMessage("Starting game...", c));
						g.init();
					} catch (Exception e) {
						Bot.incrementError();
						e.printStackTrace();
					}
				}
				return false;
			}
			boolean result = g.tick();
			if(result){
				games.remove(c);
			}
			return result;
		});
	}
}
