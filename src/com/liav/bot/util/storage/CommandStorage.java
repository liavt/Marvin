package com.liav.bot.util.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import com.liav.bot.interaction.games.BunnyGame;
import com.liav.bot.interaction.games.FishingGame;
import com.liav.bot.interaction.games.Game;
import com.liav.bot.interaction.games.GameHandler;
import com.liav.bot.interaction.user.UserInfo;
import com.liav.bot.interaction.user.Users;
import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;
import com.liav.bot.main.tasks.TaskPool;
import com.liav.bot.util.AutomodUtil;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
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
			new Command("compatible",
					"Usage: `compatible [user1] [*optional* user2]\nTests the compatibility of 2 people", "fun",
					(String[] param, IMessage c) -> {
						if (param.length == 0) {
							return "Must specify a user";
						}

						IUser user1;
						IUser user2;
						if (param.length == 1) {
							user1 = c.getAuthor();
							user2 = AutomodUtil.getUser(param[0], c.getGuild());
						} else {
							user1 = AutomodUtil.getUser(param[0], c.getGuild());
							user2 = AutomodUtil.getUser(param[1], c.getGuild());
						}

						if (user1 == null || user2 == null) {
							return "Invalid user(s)";
						} else if (user1.equals(user2)) {
							return "You need 2 different people!";
						}

						double user1ID = Long.parseLong(user1.getID());
						double user2ID = Long.parseLong(user2.getID());

						double rating1 = (user1ID / user2ID) * 10.0;
						double rating2 = (user2ID / user1ID) * 10.0;

						String finalRating = Double.toString(2.0 * (((rating1 / rating2) * 10.0) - 10.0));

						return "The compatibility of " + user1.getName() + " and " + user2.getName() + " is "
								+ finalRating.substring(0, finalRating.indexOf('.') + 2) + "/10.0";
					}),
			new Command("reverse", "Usage: `reverse [phrase]`\nReverses a string", "util", (String[] param) -> {
				if (param.length == 0) {
					return "Must input a phrase";
				}
				StringBuilder b = new StringBuilder(param[0]);
				for (int i = 1; i < param.length; i++) {
					b.append(" " + param[i]);
				}
				return b.reverse().toString();
			}),
			new Command("help",
					"Usage: `help [*optional* command]`\nIf you haven't figured it out already:\nExplains a command,or views all commands",
					"meta", StringCommand.getHelpCommand()),
			new Command("pester", "Usage: `pester`\nPesters the bot, causing it to respond to you", "fun", true,
					(String[] p) -> {
						return QuoteStorage.alerts[Bot.random.nextInt(QuoteStorage.alerts.length - 1)];
					}),
			new Command("annoy", "Usage: `annoy`\nAnnoys the bot, causing it to respond to you", "fun", true,
					(String[] p) -> {
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
			new Command("hack", "Usage: `hack [player]`\nHax0rs someone", "fun", (String[] param, IMessage m) -> {
				if (param.length <= 0 || param[0] == null) {
					return "Must specify a player to hack";
				}
				final StringBuilder sb = new StringBuilder();
				final IUser u = AutomodUtil.getUser(param[0], m.getGuild());
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
					(final String[] p, final IMessage m) -> {
						final IUser u;
						if (p.length >= 1) {
							u = AutomodUtil.getUser(p[0], m.getGuild());
						} else {
							u = m.getAuthor();
						}
						if (u == null) {
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
						return "Created by Liav Turkia with ❤\nSource code at https://www.github.com/liavt/marvin\nTo invite your server, open this link:\nhttps://discordapp.com/oauth2/authorize?client_id=199977541635801088&scope=bot&permissions=271711254";
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
					"Usage: `say [*optional* tts][phrase]`\nMake the bot say a phrase\ntts makes the message utilize TTS (Text-to-Speech)",
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
						if (p[0].equals("tts")) {
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
			new Command("ping", "Usage: `ping`\nPong!", "fun", (final String[] p, final IMessage m) -> {
				return "Pong!";
			}), new Command("pong", "`Usage: `pong`\nWhen ping isn't good enough for you", "fun",
					(final String[] p, final IMessage m) -> {
						return "Ping?";
					}),
			new Command("ding", "Usage: `ding`\nDing dong!", "fun", (final String[] p, final IMessage m) -> {
				return "Dong!";
			}), new Command("dong", "Usage: `dong`\nDing dong!", "fun", (final String[] p, final IMessage m) -> {
				return "( ͡° ͜ʖ ͡°) Ding!";
			}), new Command("balance", "Usage: `balance`\nCheck your current balance", "economy",
					(final String[] p, final IUser u) -> {
						return "Your current balance is $" + Users.getInfo(u).getMoney();
					}),
			new Command("give", "Usage: `give [amount] [users]`\nGives a certain amount of money to someone", "economy",
					(final String[] p, final IMessage m) -> {
						if (p.length == 0) {
							return "Must have an amount and a user!";
						} else if (p.length != 2) {
							return "Invalid argument! Use `" + Configuration.COMMAND_PREFIX
									+ "help give` to learn how this command work.";
						}

						long amount = 0;
						try {
							amount = Long.parseLong(p[0]);
						} catch (NumberFormatException e) {
							return "Amount must be a number!";
						}

						if (amount <= 0) {
							return "Amount must be greater than 0!";
						}

						IUser sendee = m.getAuthor();
						IUser reciever = AutomodUtil.getUser(p[1], m.getGuild());
						if (reciever == null) {
							return "Invalid person to give money to.";
						} else if (sendee.equals(reciever)) {
							return "Can\'t send money to yourself!";
						}

						UserInfo sendeeInfo = Users.getInfo(sendee);
						UserInfo recieverInfo = Users.getInfo(reciever);

						if (sendeeInfo.getMoney() < amount) {
							return "You do not have enough money to send $" + amount + "!";
						}

						sendeeInfo.addMoney(-amount);
						recieverInfo.addMoney(amount);

						return sendee.getName() + " has sent $" + amount + " to " + reciever.getName() + ".\n "
								+ sendee.getName() + " now has $" + sendeeInfo.getMoney() + " and " + reciever.getName()
								+ " now has $" + recieverInfo.getMoney();
					}),
			new Command("emojify", "Usage: `emojify [phrase]`\nConverts a phrase into emoji", "fun",
					(final String[] p) -> {
						if (p.length == 0) {
							return "Must input a phrase!";
						}

						String output = "";

						for (int i = 0; i < p.length; ++i) {
							output += AutomodUtil.stringToEmoji(p[i]);
							output += " ";
						}

						return output;
					}),
			new Command("game",
					"Usage: `game start [game] [*optional*seconds to start]` or `game join` or `game list`\nCreate a new game or join one.\n`game list` shows the possible games you can play.",
					"fun", (final String[] param, IMessage m) -> {
						if (param.length == 0) {
							return "Must include arguments.";
						} else if (param[0].equals("start")) {
							if (GameHandler.hasGame(m.getChannel())) {
								return "Game already running!";
							}

							if (param.length < 2) {
								return "Must have a game name for start";
							}

							Game game = null;
							if (param[1].equals("bunny")) {
								game = new BunnyGame();
							} else if (param[1].equals("fish")) {
								game = new FishingGame();
							} else {
								return "Invalid game. Use `" + Configuration.COMMAND_PREFIX
										+ "game list` to view available games.";
							}

							if (param.length >= 3) {
								try {
									int gameTime = Integer.parseInt(param[2]);
									if (gameTime < 5) {
										return "Too short of a game time!";
									} else if (gameTime > 30) {
										return "Too long of a game time!";
									}

									game.setTimeUntilStart(gameTime * 1000);
								} catch (NumberFormatException e) {
									return "Game time must be a number";
								}
							} else {
								game.setTimeUntilStart(Configuration.DEFAULT_GAME_STARTUP);
							}

							GameHandler.addGame(game, m.getChannel());
							GameHandler.getGame(m.getChannel()).addUser(m.getAuthor());
							;

							return "Started game. Use `" + Configuration.COMMAND_PREFIX + "game join` to join.";
						} else if (param[0].equals("join")) {
							if (!GameHandler.hasGame(m.getChannel())) {
								return "No game running!";
							}

							Game g = GameHandler.getGame(m.getChannel());

							if (g.hasStarted()) {
								return "The game has already started!";
							} else if (g.getUsers().contains(m.getAuthor())) {
								return "You are already in the game!";
							} else if (g.getUsers().size() >= Configuration.MAX_GAME_SIZE) {
								return "There are too many people in this game already.";
							}
							g.addUser(m.getAuthor());

							return "Joined the game!";
						} else if (param[0].equals("list")) {
							return "**Available games:**\nBunny - Totally legal bunny racing\nFish - Whoever gets the fish wins!";
						}

						return "Invalid argument.";
					}),
			new Command("leaderboard", "Usage: `leaderboard`\nView the richest people on this Discord!", "economy",
					(final String[] p, final IMessage m) -> {
						List<IUser> guildUsers = m.getGuild().getUsers();
						Map<IUser, UserInfo> users = Users.getUsers();
						ArrayList<Entry<IUser, UserInfo>> leaderboard = new ArrayList<>();

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

						String out = "**🏆Server Leaderboad🏆**\n";
						int i = 0;
						for (Entry<IUser, UserInfo> e : leaderboard) {
							out += (++i) + " - " + e.getKey().getName() + " with $" + e.getValue().getMoney() + "\n";
							if (i >= 10) {
								break;
							}
						}

						return out;
					}),
			new Command("gamble", "Usage: gamble [amount]\nGamble an amount for the chance to win it big!", "fun",
					(final String[] p, final IMessage m) -> {
						if (p.length == 0) {
							return "Must provide a monetary amount!";
						}

						UserInfo info = Users.getInfo(m.getAuthor());
						long amount = 0;

						if (p[0].equals("all")) {
							amount = info.getMoney();
						} else {
							try {
								amount = Long.parseLong(p[0]);
							} catch (NumberFormatException e) {
								return "Amount must be a number!";
							}
						}

						if (amount <= 0) {
							return "Amount must be greater than 0!";
						}

						if (info.getMoney() < amount) {
							return "You do not have enough money to gamble $" + amount + "!";
						}

						int success = -2;

						String roll = Long.toString(Math.abs(Bot.random.nextLong()));
						String ID = m.getAuthor().getID();
						for (int i = 0; i < ID.length(); ++i) {
							if (i < roll.length() && (roll.charAt(i) == ID.charAt(i)
									|| (i > 0 && roll.charAt(i) == ID.charAt(i - 1)))) {
								++success;
							}
						}

						if (success < 0) {
							success = 0;
						}

						String output = "";

						long reward = success * amount;

						if (success == 0) {
							output = "Oh no! You lost it all.";
						} else if (success == 1) {
							output = "You didn\'t lose or gain anything.";
						} else if (success > 1 && success < 4) {
							reward = (long) Math.ceil(amount * (1.0 + (success * 0.3)));
							output = "You got some money! ";
						} else if (success >= 4) {
							output = "JACKPOT! ";
						}

						info.addMoney(reward - amount);

						return output + " You got $" + reward + ". You are now at $" + info.getMoney();
					}),
			new Command("daily", "Usage: `daily`\nUse once a day to get $100 for free!", "economy",
					(final String[] p, final IUser u) -> {
						final UserInfo info = Users.getInfo(u);

						if (!info.isDailyAvailable()) {
							return "You can claim your daily in `"
									+ AutomodUtil.timeToString((int) (info.timeUntilNextDaily() / 1000)) + '`';
						}

						info.doDaily();

						return "You have gained $100! Your new balance is $" + info.getMoney();
					}),
			new Command("chuck",
					"Usage: `chuck [*optional* person]`\nGet a Chuck Norris joke\nThe joke will feature a user if specified\n*Jokes from http://www.icndb.com/*",
					"fun", (final String[] p, final IMessage m) -> {
						if (p.length > 1) {
							return "Can only have one person!";
						}
						String arguments = "";
						if (p.length == 1) {
							final IUser user = AutomodUtil.getUser(p[0], m.getGuild());
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
