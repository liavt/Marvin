package com.liav.bot.util.storage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import com.liav.bot.main.Bot;

public class FateStorage {
	public static String[] getFates() {
		try {
			try (final Stream<String> s = Files.lines(Paths.get("fates.txt"))) {
				final ArrayList<String> out = new ArrayList<>();
				final Iterator<String> i = s.iterator();
				while (i.hasNext()) {
					out.add(i.next());
				}
				return out.toArray(new String[out.size()]);
			}
		} catch (Throwable e) {
			Bot.incrementError();
			e.printStackTrace();
		}
		return null;
	}
}
