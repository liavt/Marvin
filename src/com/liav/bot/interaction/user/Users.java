package com.liav.bot.interaction.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class Users {
	private static Map<IUser, UserInfo> users = new HashMap<>();

	public static Map<IUser, UserInfo> getUsers() {
		return users;
	}

	public static UserInfo getInfo(final IUser u) {
		if (!users.containsKey(u)) {
			users.put(u, new UserInfo(u));
		}

		return users.get(u);
	}

	public static void save() throws IOException {
		try (final FileWriter writer = new FileWriter(new File(Configuration.USERS_FILE))) {
			for (Entry<IUser, UserInfo> e : users.entrySet()) {
				writer.write(e.getKey().getLongID() + ":" + e.getValue().getData() + System.lineSeparator());
			}
		}
	}

	public static void load() throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(Configuration.USERS_FILE)))) {
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				String[] data = buffer.split(":");
				IUser u = Bot.getClient().getUserByID(Long.parseLong(data[0]));
				UserInfo i = new UserInfo(u, data[1]);
				users.put(u, i);
			}
		}
	}

	public static ArrayList<Entry<IUser, UserInfo>> getLevelboard(IGuild g) {
		final List<IUser> guildUsers = g.getUsers();
		final Map<IUser, UserInfo> users = Users.getUsers();
		final ArrayList<Entry<IUser, UserInfo>> leaderboard = new ArrayList<>();

		for (Entry<IUser, UserInfo> e : users.entrySet()) {
			if (guildUsers.contains(e.getKey())) {
				leaderboard.add(e);
			}
		}

		// sorting a map by a custom value... only java makes it
		// this easy
		Collections.sort(leaderboard, new Comparator<Entry<IUser, UserInfo>>() {
			@Override
			public int compare(Entry<IUser, UserInfo> left, Entry<IUser, UserInfo> right) {
				return (int) (right.getValue().getTotalXp() - left.getValue().getTotalXp());
			}
		});

		return leaderboard;
	}

	public static ArrayList<Entry<IUser, UserInfo>> getLeaderboard(IGuild g) {
		final List<IUser> guildUsers = g.getUsers();
		final Map<IUser, UserInfo> users = Users.getUsers();
		final ArrayList<Entry<IUser, UserInfo>> leaderboard = new ArrayList<>();

		for (Entry<IUser, UserInfo> e : users.entrySet()) {
			if (guildUsers.contains(e.getKey())) {
				leaderboard.add(e);
			}
		}

		// sorting a map by a custom value... only java makes it
		// this easy
		Collections.sort(leaderboard, new Comparator<Entry<IUser, UserInfo>>() {
			@Override
			public int compare(Entry<IUser, UserInfo> left, Entry<IUser, UserInfo> right) {
				int leftRank = (left.getValue().getLevelRank(g) + left.getValue().getMoneyRank(g)) / 2;
				int rightRank = (right.getValue().getLevelRank(g) + right.getValue().getMoneyRank(g)) / 2;
				
				return leftRank - rightRank;
			}
		});

		return leaderboard;
	}

	public static ArrayList<Entry<IUser, UserInfo>> getMoneyboard(IGuild g) {
		final List<IUser> guildUsers = g.getUsers();
		final Map<IUser, UserInfo> users = Users.getUsers();
		final ArrayList<Entry<IUser, UserInfo>> leaderboard = new ArrayList<>();

		for (Entry<IUser, UserInfo> e : users.entrySet()) {
			if (guildUsers.contains(e.getKey())) {
				leaderboard.add(e);
			}
		}

		// sorting a map by a custom value... only java makes it
		// this easy
		Collections.sort(leaderboard, new Comparator<Entry<IUser, UserInfo>>() {
			@Override
			public int compare(Entry<IUser, UserInfo> left, Entry<IUser, UserInfo> right) {
				return (int) (right.getValue().getMoney() - left.getValue().getMoney());
			}
		});

		return leaderboard;
	}
}
