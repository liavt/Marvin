package com.liav.bot.util.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.script.ScriptException;

// S  U  C  C
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liav.bot.interaction.commands.CategoryHandler;
import com.liav.bot.interaction.commands.CategoryHandler.Category;
import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.interaction.commands.interfaces.StringCommand;
import com.liav.bot.main.Bot;
import com.liav.bot.main.tasks.TaskPool;
import com.liav.bot.util.AutomodUtil;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.audio.AudioPlayer;

/**
 * Static class which contains all the commands for use by the bot. Located here
 * for organizational's sake.
 * 
 * @author Liav
 * @see Command
 * @see CommandHandler
 */
public final class CommandStorage {
	private CommandStorage() {
	}

	/**
	 * List of all the possible {@link Command commands.}
	 * 
	 * @see CommandHandler
	 */
	public final static Command[] commands = {
			new Command("newfag", "Usage: `newfag`\nBe the fag of the new generation with the newfag command", "fun",
					(String[] param) -> {
						return "newfag is NOT a command and will NEVER be a command";
					}),
			new Command("roll",
					"Usage: `roll [*optional* bound]`\nImagine rolling the dice. Now imagine not actually rolling a die, but imaginarily rolling it. You will get it",
					"math", (String[] param) -> {
						if (param.length <= 0 || param[0] == null) {
							return "Rolled " + Bot.random.nextInt();
						} else if (param.length > 1) {
							return "Must specify one number";
						}
						final int number = Integer.valueOf(param[0]);
						if (number <= 0) {
							return "Invalid number";
						}
						return "Rolled a `" + Bot.random.nextInt(number) + "` out of `" + param[0] + "`";
					}),
			new Command("help",
					"Usage: `help [*optional* command]`\nIf you haven't figured it out already:\nExplains a command,or views all commands",
					"meta", StringCommand.getHelpCommand()),
			new Command("pester", "Usage: `pester`\nPesters the bot, causing it to respond to you", "fun", true,
					(String[] p, IUser u) -> {
						return QuoteStorage.alerts[Bot.random.nextInt(QuoteStorage.alerts.length - 1)];
					}),
			new Command("annoy", "Usage: `annoy`\nAnnoys the bot, causing it to respond to you", "fun", true,
					(String[] p, IUser u) -> {
						return QuoteStorage.warnings[Bot.random.nextInt(QuoteStorage.warnings.length - 1)];
					}),
			new Command("pleb", "Usage: `pleb`\nPleb.", "fun", (String[] param) -> {
				return "Glorious pleb.";
			}),
			new Command("why", "Usage: `why`\nAsk the bot why it did this.", "fun", true, (String[] param, IUser u) -> {
				return "BECAUSE IT\'S THE FINAL COUNTDOWN";
			}),
			new Command("how", "Usage: `how`\nAsk the bot how it got here", "fun", true, (String[] param, IUser u) -> {
				return "With slave labor, of course.";
			}), new Command("when", "Usage: `when`\nAsk the bot when it will stop.", "fun", true,
					(String[] param, IUser u) -> {
						return "AFTER 10,000 YEARS";
					}),
			new Command("where", "Usage: `where`\nAsk the bot where it is located.", "fun", true,
					(String[] param, IUser u) -> {
						return "Over the rainbow.";
					}),
			new Command("hack", "Usage: `hack [player]`\nHax0rs someone", "fun", (String[] param) -> {
				if (param.length <= 0 || param[0] == null) {
					return "Must specify a player to hack";
				}
				final StringBuilder sb = new StringBuilder();
				final IUser u = AutomodUtil.getUser(param[0]);
				if (u == null) {
					return param[0] + " cannot be hacked, as " + param[0]
							+ " couldn\'t be found in the triangulating mainframe.";
				}
				final String id = u.getID();
				sb.append("Succesfully hacked ").append(u.getName()).append(". Results are as followed: ")
						.append("\n`");
				sb.append(AutomodUtil.timeToString((int) Long.parseLong(id.substring(0, 7)))).append("` of porn")
						.append("\n`");
				sb.append(AutomodUtil.timeToString((int) Long.parseLong(id.substring(8, 15))))
						.append("` of dank memes,").append("\n`");
				sb.append(AutomodUtil.timeToString((int) Long.parseLong(id.substring(5, 10)))).append("` of sex tapes,")
						.append("\n`");
				sb.append("$").append((int) Long.parseLong(id.substring(3, 6))).append("` stolen,").append("\nAnd `");
				sb.append((int) Long.parseLong(id.substring(15, 16))).append("` hot singles in ").append(u.getName())
						.append("\'s area").append("\n");
				return sb.toString();
			}), new Command("eval", "Usage: `eval [expression]`\nCalculates a mathmatical expression for you.", "math",
					(final String[] param) -> {
						if (param.length == 0) {
							return "Must specify a mathmatical expression";
						}
						final StringBuilder sb = new StringBuilder();
						for (final String s : param) {
							// make sure that they aren't inserting any
							// javascript functions (that would be very bad.)
							// You can't really do anything without letters
							if (Pattern.matches("([0-9]|\\.|\\+|\\^|\\-|/|\\%|\\*|\\=|\\(|\\))+", s)) {
								sb.append(s);
							} else {
								return "Can only use numbers and mathmatical operations";
							}
						}
						try {
							return sb.toString() + " evaluates to `" + AutomodUtil.evaluateExpression(sb.toString())
									+ "`";
						} catch (ScriptException e)

				{
							e.printStackTrace();
							Bot.incrementError();
							return "Invalid expression!";
						}
					}),
			new Command("category",
					"Usage: `category [*optional* category name]`\nLists all command categories, or shows all commands in a specified category",
					"meta", (final String[] param) -> {
						final StringBuilder sb = new StringBuilder();
						if (param.length <= 0) {
							final Category[] cats = CategoryHandler.getCategories();
							int iterator = 0;
							sb.append("Command Categories: \n");
							for (final Category c : cats) {
								iterator++;
								sb.append("**").append(c.getName()).append(" (").append(c.getCommands().length)
										.append(" command").append(c.getCommands().length == 1 ? "" : "s")
										/* for plurals */.append(")**	");
								// allow 5 names per line
								if (iterator % 5 == 0) {
									sb.append("\n");
								}
							}
							sb.append("\nUse `category [name]` to view all commands in a certain category ");
						} else if (param.length >= 2) {
							return "Can only have one parameter. Refer to `help category` for more.";
						} else {
							final Category c = CategoryHandler.getCategory(param[0]);
							if (c == null) {
								return "Invalid category";
							}
							sb.append("Commands in category ").append(c.getName()).append(":\n**");
							int iterator = 0;
							for (final Command com : c.getCommands()) {
								sb.append(com.getName()).append("	");
								iterator++;
								if (iterator % 8 == 0) {
									sb.append("\n");
								}
							}
							sb.append("**");
						}
						return sb.toString();
					}),
			new Command("fate", "Usage: `fate [*optional* player]`\nTells the fate of a person.", "fun",
					(final String[] p, final IUser user) -> {
						final IUser u;
						if (p.length >= 1) {
							u = AutomodUtil.getUser(p[0]);
						} else {
							u = user;
						}
						if(u==null){
							return "Invalid user";
						}
						final String[] fates = FateStorage.getFates();
						if (fates == null) {
							return "Error retrieving list of fates.";
						}
						return fates[Bot.random.nextInt(fates.length)].replace("%U%", u.getName());
					}),
			new Command("succ", "Usage: `succ`\n*succ*", "fun", (final String[] p, final IUser user) -> {
				String finRes = "";
				Vector<String> strings = new Vector<>();

				try {
					final String googleUrl = "https://imgur.com/a/B4pQe/hit?scrolled";
					final Document doc = Jsoup.connect(googleUrl)
							.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
							.timeout(10 * 1000).get();
					final Elements media = doc.select(".post-image-container");

					for (final Element src : media) {
						strings.add(src.attr("id").toString());
					}

					final String[] array = strings.toArray(new String[strings.size()]);
					final int rnd = Bot.random.nextInt(array.length);
					finRes = "http://i.imgur.com/" + (array[rnd]).toString() + ".png";

				} catch (IOException e) {
					Bot.incrementError();
					e.printStackTrace();
					return "Error reading from Imgur!";
				} catch (Throwable e) {
					Bot.incrementError();
					e.printStackTrace();
					return "An error occured.";
				}

				return finRes;
			}), new Command("about", "Usage: `about`\nLearn about the bot", "meta",
					(final String[] p, final IUser user) -> {
						return "Created by Liav Turkia with <3\nSource code at https://www.github.com/liavt/marvin\nTo invite your server, open this link:\nhttps://discordapp.com/oauth2/authorize?client_id=199977541635801088&scope=bot&permissions=271711254";
					}),
			new Command("status", "Usage: `status`\nView uptime, errors, and various debug values", "meta", false, true,
					(final String[] p, final IUser u) -> {
						return "Up for `"
								+ AutomodUtil.timeToString(Integer.parseInt(String.format("%d",
										TimeUnit.MILLISECONDS
												.toSeconds(System.currentTimeMillis() - Bot.getStartTime()))))
								+ "`\nWith `" + Bot.getErrors() + "` errors and `" + Bot.getCommands()
								+ "` commands executed." + "\nCurrent task pool size: `" + TaskPool.tasks() + "`";
					}),
			new Command("say",
					"Usage: `say [*optional* -t][phrase]`\nMake the bot say a phrase\n-t makes the message utilize TTS (Text-to-Speech)",
					"util", false, true, (final String[] p, final IMessage m) -> {
						if (p.length < 1) {
							return "Must have a parameter!";
						}
						try {
							m.delete();
						} catch (DiscordException e) {
							e.printStackTrace();
							Bot.incrementError();
							return "An error in Discord occured.";
						} catch (RateLimitException e) {
							e.printStackTrace();
							Bot.incrementError();
							return "Too many requests! Try again later.";
						} catch (MissingPermissionsException e) {
							e.printStackTrace();
							Bot.incrementError();
						}

						final boolean tts;
						if (p[0].equals("-t")) {
							tts = true;
						} else {
							tts = false;
						}
						final StringBuilder sb = new StringBuilder();
						for (int i = tts ? 1 : 0; i < p.length; i++) {
							sb.append(p[i]).append(" ");
						}

						try {
							Bot.sendMessage(sb.toString(), tts, m.getChannel());
						} catch (DiscordException e) {
							e.printStackTrace();
							Bot.incrementError();
							return "An error in Discord occured.";
						} catch (RateLimitException e) {
							e.printStackTrace();
							Bot.incrementError();
							return "Too many requests! Try again later.";
						} catch (MissingPermissionsException e) {
							e.printStackTrace();
							Bot.incrementError();
						}

						return "";
					}),
			new Command("ping", "`Usage: ping`\nPong!", "fun", (final String[] p, final IMessage m) -> {
				return "Pong!";
			}), new Command("pong", "`Usage: pong`\nWhen ping isn't good enough for you", "fun",
					(final String[] p, final IMessage m) -> {
						return "Ping?";
					}),
			new Command("ding", "Usage: ding\nDing dong!", "fun", (final String[] p, final IMessage m) -> {
				return "Dong!";
			}), new Command("dong", "Usage: dong\nDing dong!", "fun", (final String[] p, final IMessage m) -> {
				return "( ͡° ͜ʖ ͡°) Ding!";
			}),
			new Command("chuck",
					"Usage: `chuck [*optional* person]`\nGet a Chuck Norris joke\nThe joke will feature a user if specified\n*Jokes from http://www.icndb.com/*",
					"fun", (final String[] p, final IMessage m) -> {
						if (p.length > 1) {
							return "Can only have one person!";
						}
						String arguments = "";
						if (p.length == 1) {
							final IUser user = AutomodUtil.getUser(p[0]);
							if (user == null) {
								return "User doesn't have a name or doesn't exist!";
							}
							final String name = user.getName();
							String firstName = "", lastName = "";
							if (name.contains(" ")) {
								final String[] split = name.split(" ");
								firstName = split[0];
								lastName = split[1];
							} else {
								lastName = "Norris";
								firstName = name;
							}
							arguments += "?firstName=" + firstName + "&lastName=" + lastName;
						}
						try {
							final URL url = new URL("https://api.icndb.com/jokes/random" + arguments);
							final HttpURLConnection request = (HttpURLConnection) url.openConnection();
							request.connect();

							final JsonParser jp = new JsonParser(); // from gson
							final JsonElement root = jp
									.parse(new InputStreamReader((InputStream) request.getContent())); // Convert
																										// the
																										// input
																										// stream
																										// to
																										// a
																										// json
																										// element
							final JsonObject rootobj = root.getAsJsonObject(); // May
																				// be
																				// an
																				// array,
																				// may
																				// be
																				// an
																				// object.
							return Jsoup.parse(
									rootobj.getAsJsonObject().get("value").getAsJsonObject().get("joke").getAsString())
									.text();
						} catch (IOException e) {
							Bot.incrementError();
							e.printStackTrace();
							return "Error reading from joke server!";
						}
					})
	};
}
