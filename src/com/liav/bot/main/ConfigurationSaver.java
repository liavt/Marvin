package com.liav.bot.main;

import com.liav.bot.main.tasks.TaskPool;

public class ConfigurationSaver implements Runnable {
	@Override
	public void run() {
		try {
			synchronized (this) {
				while (true) {
					wait(Configuration.SAVE_TIME);
					Configuration.save();
				}
			}
		} catch (Throwable e) {
			Bot.incrementError();
			e.printStackTrace();
		}
	}
}
