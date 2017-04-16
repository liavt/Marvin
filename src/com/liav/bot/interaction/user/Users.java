package com.liav.bot.interaction.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;

import sx.blah.discord.handle.obj.IUser;

public class Users {
	private static Map<IUser, UserInfo> users = new HashMap<>();
	
	public static Map<IUser, UserInfo> getUsers(){
		return users;
	}

	public static UserInfo getInfo(final IUser u) {
		if (!users.containsKey(u)) {
			users.put(u, new UserInfo());
		}

		return users.get(u);
	}

	public static void save() throws IOException {
		try(final FileWriter writer = new FileWriter(new File(Configuration.USERS_FILE))){
			for (Entry<IUser, UserInfo> e : users.entrySet()) {
				writer.write(e.getKey().getID() + ":" + e.getValue().getData() + System.lineSeparator());
			}
		}
	}

	public static void load() throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(Configuration.USERS_FILE)))) {
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				String[] data = buffer.split(":");
				IUser u = Bot.getClient().getUserByID(data[0]);
				UserInfo i = new UserInfo(data[1]);
				users.put(u, i);
			}
		}
	}
}
