package com.liav.bot.util;

import java.util.EnumSet;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import com.liav.bot.main.Bot;

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
			int minutes = (int) Math.floor(time / 60), seconds = time
			        - (minutes * 60);
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
	 * @return The specified user in the bot's {@link IGuild}. If no user was
	 *         found, returns null.
	 */
	public static IUser getUser(String s) {
		final List<IGuild> current = Bot.getClient().getGuilds();
		for (IGuild g : current) {
			for (IUser u : g.getUsers()) {
				if (u.getName().equals(s) || u.getID().equals(s)
				        || ("<@" + u.getID() + ">").equals(s)) { return u; }
			}
		}
		return null;
	}

	/**
	 * Converts a string to a mathematical expression and evaluates it with
	 * Javascritp.
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

	public static boolean isAdmin(IUser i, IGuild g) {
		final List<IRole> l = i.getRolesForGuild(g);
		for (IRole r : l) {
			final EnumSet<Permissions> e = r.getPermissions();
			for (Permissions p : e) {
				if (p.hasPermission(Permissions.MANAGE_ROLES.ordinal())) { return true; }
			}
		}
		return false;
	}
}
