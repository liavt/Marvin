package com.liav.bot.listeners;

import java.util.Scanner;

import com.liav.bot.interaction.commands.CommandHandler;

import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class ConsoleListener implements Runnable{
	@Override
	public void run(){
		try(Scanner scanner = new Scanner(System.in)){
			while(true){
				String input = scanner.nextLine();
				System.out.println(CommandHandler.handleMention(input));
			}
		}
	}
}
