package com.liav.bot.util.storage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class FateStorage {
	public static String[] getFates() {
		try {
			final Stream<String> s = Files.lines(Paths.get("fates.txt"));
			try {
				final ArrayList<String> out = new ArrayList<>();
				final Iterator<String> i = s.iterator();
				while (i.hasNext()) {
					out.add(i.next());
				}
				return out.toArray(new String[out.size()]);
			} finally {
				if (s != null) {
					s.close();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
