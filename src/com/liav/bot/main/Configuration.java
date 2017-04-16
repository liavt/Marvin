package com.liav.bot.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.liav.bot.interaction.user.Users;
import com.liav.bot.main.tasks.Task;

public class Configuration {
	/**
	 * @see #getCommandPrefix
	 */
	public static final String COMMAND_PREFIX = ";";

	public static final String OWNER = "78544340628013056";

	public static final String USERS_FILE = "users.txt";
	public static final String CONFIG_FILE = "config.txt";
	/**
	 * Amount of milliseconds in a daily (one day)
	 */
	public static final long DAILY_TIME = 86400000L;

	/**
	 * How many milliseconds it will take for tasks to get their
	 * {@link Task#tick()} method called.
	 */
	public static final int UPDATE_RATE = 50;

	public static final long SAVE_TIME = 300000L;

	public static Map<String, String> properties = new HashMap<>();

	@SuppressWarnings("resource")
	public static void init() throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(Configuration.CONFIG_FILE)))) {
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				String[] values = buffer.split("=");
				if (values.length != 2) {
					reader.close();
					throw new IllegalArgumentException("Invalid config.txt");
				}
				properties.put(values[0], values[1]);
			}
		}
	}

	public static void load() throws IOException {
		Users.load();
		System.out.println("Loaded from file.");
	}

	public static void save() throws IOException {
		Users.save();
		System.out.println("Saved to file.");
	}
}
