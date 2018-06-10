package com.liav.bot.main;

public class ConfigurationSaver implements Runnable {
	@Override
	public void run() {
		try {
			synchronized (this) {
				while (true) {
					wait(Configuration.SAVE_TIME);
					if(Bot.wasUpdated()){
						Configuration.save();
						Bot.setUpdated(false);
					}
				}
			}
		} catch (Throwable e) {
			Bot.incrementError();
			e.printStackTrace();
		}
	}
}
