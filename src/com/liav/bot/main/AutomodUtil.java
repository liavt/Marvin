package com.liav.bot.main;

import java.util.EnumSet;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.liav.bot.interaction.user.Users;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Class with various static helper functions used throughout the bot.
 * 
 * @author Liav
 *
 */
public final class AutomodUtil {
	private AutomodUtil() {
	}

	/**
	 * Converts seconds to a more friendly strings. Will convert up to days.
	 * 
	 * @param time
	 *            Time in seconds
	 * @return A friendly string, which converts the seconds into days, hours,
	 *         minutes, and seconds.
	 */
	public static String timeToString(int time) {
		String message = "";
		if (time > 60) {
			int minutes = (int) Math.floor(time / 60), seconds = time - (minutes * 60);
			if (minutes > 60) {
				int hours = (int) Math.floor(minutes / 60);
				minutes -= (hours * 60);
				if (hours > 24) {
					int days = (int) Math.floor(hours / 24);
					message += days + " day";
					if (days > 1) {
						message += "s";
					}
					message += ", ";
					hours -= (days * 24);
				}
				message += hours + " hour";
				if (hours > 1) {
					message += "s";
				}
				message += ", ";
			}
			message += minutes + " minute";
			if (minutes > 1) {
				message += "s";
			}
			if (seconds > 0) {
				message += " and " + (seconds) + " seconds";
			}
		} else {
			message += time + " seconds";
		}
		return message;
	}

	/**
	 * Finds the {@link IUser} associated with the string. Checks in various
	 * formats.
	 * 
	 * @param s
	 *            String to be processed, either as the full name, the user ID,
	 *            or the {@literal @mention} for the user.
	 * @param g 
	 * @return The specified user in the bot's {@link IGuild}. If no user was
	 *         found, returns null.
	 */
	public static IUser getUser(String s, IGuild g) {
		for (IUser u : g.getUsers()) {
			if (s.equals(u.getStringID()) || u.getName().equalsIgnoreCase(s) || u.getDisplayName(g).equalsIgnoreCase(s)
					|| u.getDiscriminator().equalsIgnoreCase(s) || u.mention(false).equals(s)
					|| u.mention(true).equals(s)) {
				return u;
			}
		}

		return null;
	}

	public static long getAmount(String in, IUser u) {
		String s = in;
		if (s.equals("none") || s.equals("zero")) {
			return 0;
		} else if (s.equals("all") || s.equals("everything")) {
			return Users.getInfo(u).getMoney();
		} else if (s.equals("half")) {
			return Users.getInfo(u).getMoney() / 2;
		} else if (s.startsWith("$") || s.startsWith("£")) {
			return Long.parseLong(s.substring(1));
		} else if (s.endsWith("$") || s.endsWith("£")) {
			return Long.parseLong(s.substring(0, s.length() - 1));
		} else if (s.startsWith("%") || s.endsWith("%")) {
			if (s.startsWith("%")) {
				s = s.substring(1);
			} else {
				s = s.substring(0, s.length() - 1);
			}
			int percentage;
			try {
				percentage = Integer.parseInt(s);
			} catch (@SuppressWarnings("unused") NumberFormatException e) {
				return -1;
			}
			return (long) (Users.getInfo(u).getMoney() * (percentage / 100.0));
		}
		try {
			return Long.parseLong(s);
		} catch (@SuppressWarnings("unused") NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Converts a string to a mathematical expression and evaluates it with
	 * Javascript.
	 * 
	 * @param s
	 *            String form of a mathmatical expression. Must be able to
	 *            evaluate in Javascript
	 * @return the result
	 * @throws ScriptException
	 *             If the expression is unparseable
	 */
	public static double evaluateExpression(String s) throws ScriptException {
		final ScriptEngineManager mgr = new ScriptEngineManager();
		final ScriptEngine engine = mgr.getEngineByName("JavaScript");
		final double result = Double.valueOf("" + engine.eval(s));
		return result;
	}

	/**
	 * Get whether the user is considered an "admin" by the bot
	 * <p>
	 * An admin is a {@linkplain IUser} that is able to
	 * {@link Permissions#MANAGE_ROLES}
	 * 
	 * @param i
	 *            The user to check
	 * @param g
	 *            Which guild to check the user's privileges against
	 * @return Whether the bot is considered an admin.
	 */
	public static boolean isAdmin(IUser i, IGuild g) {
		if (i.getLongID() == (Long.parseLong(Configuration.properties.get("OWNER"))))
			return true;
		final List<IRole> l = i.getRolesForGuild(g);

		for (IRole r : l) {
			final EnumSet<Permissions> e = r.getPermissions();
			for (Permissions p : e) {
				if (p.equals(Permissions.MANAGE_ROLES)) {
					return true;
				}
			}
		}
		return false;
	}

	public static IVoiceChannel getVoiceChannel(final IGuild g, final String s) {
		IVoiceChannel c = null;
		
		try {
			c = g.getVoiceChannelByID(Long.parseLong(s));
		} catch (@SuppressWarnings("unused") NumberFormatException e) {
			/* ignore */
		}

		if (c == null) {
			List<IVoiceChannel> channels = g.getVoiceChannelsByName(s);
			if (channels.size() == 1) {
				c = channels.get(0);
			}
		}

		return c;
	}
	
	public static String stringToEmoji(final String s) {
		String output = "";

		for (int i = 0; i < s.length(); ++i) {
			output += characterToEmoji(s.charAt(i));
		}

		return output;
	}

	public static String characterToEmoji(char c) {
		if (Character.isDigit(c)) {
			return c + "\u20E3";
		} else if (c == '!') {
			return "❕";
		} else if (c == '?') {
			return "❔";
		} else if (c == '.') {
			return "▪";
		} else if (c == '#') {
			return "#⃣";
		} else if (c == '*') {
			return "✳";
		} else if (c == '$') {
			return "💲";
		} else if (c == '+') {
			return "➕";
		} else if (c == '-') {
			return "➖";
		} else if (c == '>') {
			return "▶";
		} else if (c == '<') {
			return "◀";
		} else if (c == ',') {
			return "🌙";
		} else if (c == '^') {
			return "🔼";
		} else if (c == ' ') {
			return " ";
		} else if (Character.isLetter(c)) {
			// zero width space to prevent the regional indicators to become
			// flags
			return "​\u200B" + new String(Character.toChars("🇦".codePointAt(0)
					+ (Character.toString(Character.toLowerCase(c)).codePointAt(0) - "a".codePointAt(0))));
		}
		return "❌";
	}
}
