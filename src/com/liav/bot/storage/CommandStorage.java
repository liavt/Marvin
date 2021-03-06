package com.liav.bot.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;

import java.util.Vector;
import java.util.Map.Entry;
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
import com.liav.bot.interaction.games.BunnyGame;
import com.liav.bot.interaction.games.FishingGame;
import com.liav.bot.interaction.games.Game;
import com.liav.bot.interaction.games.GameHandler;
import com.liav.bot.interaction.user.UserInfo;
import com.liav.bot.interaction.user.Users;
import com.liav.bot.main.AutomodUtil;
import com.liav.bot.main.Bot;
import com.liav.bot.main.Configuration;
import com.liav.bot.main.tasks.TaskPool;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.MissingPermissionsException;

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
	public final static Command[] commands = { new Command("newfag",
			"Usage: `newfag`\nBe the fag of the new generation with the newfag command", "fun", (String[] param) -> {
				return "newfag is NOT a command and will NEVER be a command";
			}),
			new Command("roll",
					"Usage: `roll [*optional* bound]`\nGives a random number.\nIf an argument is provided, gives a random number between 0 and *bound*",
					"util", (String[] param) -> {
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
					}, "random,rand,dice"),
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

						double user1ID = user1.getLongID();
						double user2ID = user2.getLongID();

						double rating1 = (user1ID / user2ID) * 10.0;
						double rating2 = (user2ID / user1ID) * 10.0;

						String finalRating = Double.toString(2.0 * (((rating1 / rating2) * 10.0) - 10.0));

						return "The compatibility of " + user1.getName() + " and " + user2.getName() + " is "
								+ finalRating.substring(0, finalRating.indexOf('.') + 2) + "/10.0";
					}, "compat"),
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
				final String id = u.getStringID();
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
			}), new Command("eval", "Usage: `eval [expression]`\nCalculates a mathmatical expression for you.", "util",
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
					}, "evaluate,calculate"),
			new Command("category",
					"Usage: `category` or `category [name]`\nLists all command categories, or shows all commands in a specified category",
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
							sb.append("\nUse `");
							sb.append(CommandHandler.getCommandPrefix());
							sb.append("category [name]` to view all commands in a certain category ");
						} else if (param.length >= 2) {
							return "Can only have one parameter. Refer to `" + CommandHandler.getCommandPrefix()
									+ "help category` for more.";
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
			new Command("fate",
					"Usage: `fate [*optional* user]`\nTells the fate of a person.\nIf no user is provided, gives a fate for yourself.",
					"fun", (final String[] p, final IMessage m) -> {
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
					finRes = "https://i.imgur.com/" + (array[rnd]).toString() + ".png";

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
			}),
			new Command("info", "Usage: `info`\nLearn about the bot", "meta", (final String[] p, final IUser user) -> {
				return "Use `" + CommandHandler.getCommandPrefix()
						+ "help` to view commands.\nCreated by Liav Turkia with :heart:\nSource code at https://www.github.com/liavt/marvin\nTo invite your server, open this link:\nhttps://discordapp.com/oauth2/authorize?client_id=199977541635801088&scope=bot&permissions=271711254";
			}, "about"), new Command("status", "Usage: `status`\nView uptime, errors, and various debug values", "meta",
					false, true, (final String[] p, final IUser u) -> {
						String output = "";
						output += "Up for `" + AutomodUtil.timeToString(Integer.parseInt(String.format("%d",
								TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - Bot.getStartTime()))));
						if (Bot.getCommands() > 0) {
							output += "`\nWith `" + Bot.getErrors() + "` errors and `" + Bot.getCommands()
									+ "` commands executed.";
							double success = 1.0d - ((double) Bot.getErrors() / (double) Bot.getCommands());
							output += "\nSuccess rate: `" + (success * 100) + "%` (";
							if (success == 1.0f) {
								output += "Perfect";
							} else if (success >= 0.9f) {
								output += "Excellent";
							} else if (success >= 0.75f) {
								output += "Great";
							} else if (success >= 0.6f) {
								output += "Good";
							} else if (success >= 0.4f) {
								output += "Mediocre";
							} else if (success >= 0.2f) {
								output += "Bad";
							} else if (success != 0.0f) {
								output += "Terrible";
							} else {
								output += "Broken";
							}
						}
						output += ")";
						output += "\nCurrent task pool size: `" + TaskPool.tasks() + "`";
						return output;
					}),
			new Command("say",
					"Usage: `say [phrase]` or `say -t [phrase]` or `say -e [phrase]`*\nMake the bot say a phrase\nOptional parameter `-e` makes the bot output the phrase emojified\nOptional parameter `-t` makes the message utilize TTS (Text-to-Speech)",
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

						final boolean tts = p[0].equals("-t");
						final boolean emoji = p[0].equals("-e");

						final StringBuilder sb = new StringBuilder();
						for (int i = tts || emoji ? 1 : 0; i < p.length; i++) {
							sb.append(p[i]).append(" ");
						}

						String message = sb.toString();
						if (emoji) {
							message = AutomodUtil.stringToEmoji(message);
						}

						try {
							Bot.sendMessage(message, tts, m.getChannel());
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
					}, "talk"),
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
			}),
			new Command(
					"balance", "Usage: `balance`\nCheck your current balance.\nSee also `"
							+ CommandHandler.getCommandPrefix() + "profile`",
					"economy", (final String[] p, final IUser u) -> {
						return "Your current balance is $" + Users.getInfo(u).getMoney();
					}, "account,money,bank"),
			new Command("coin", "Usage: `coin`\nFlip a coin for heads or tails", "util", (final String[] p) -> {
				if (Bot.random.nextBoolean()) {
					return "Heads!";
				} else {
					return "Tails!";
				}
			}, "flip"),
			new Command("give",
					"Usage: `give [user] [amount]` or `give [amount] [user]`\nGives a certain amount of money to someone",
					"economy", (final String[] p, final IMessage m) -> {
						if (p.length == 0) {
							return "Must have an amount and a user!";
						} else if (p.length != 2) {
							return "Invalid argument! Use `" + CommandHandler.getCommandPrefix()
									+ "help give` to learn how this command work.";
						}

						IUser sendee = m.getAuthor();
						IUser reciever = null;

						long amount = AutomodUtil.getAmount(p[1], m.getAuthor());
						reciever = AutomodUtil.getUser(p[0], m.getGuild());
						if (amount <= -1 && reciever == null) {
							amount = AutomodUtil.getAmount(p[0], m.getAuthor());
							reciever = AutomodUtil.getUser(p[1], m.getGuild());
						}

						if (amount <= 0) {
							return "Invalid amount to give!";
						}

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

						return sendee.getName() + " has sent $`" + amount + "` to " + reciever.getName() + ".\n"
								+ sendee.getName() + " now has $`" + sendeeInfo.getMoney() + "` and "
								+ reciever.getName() + " now has $`" + recieverInfo.getMoney() + "`";
					}, "pay"),
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
					}, "emoji"),
			new Command("level", "Usage: `level` or `level [user]`\nView your level or someone elses", "meta",
					(final String[] p, IMessage m) -> {
						IUser u = m.getAuthor();
						if (p.length >= 1) {
							u = AutomodUtil.getUser(p[0], m.getGuild());
						}

						if (u == null) {
							return "Invalid user!";
						}

						UserInfo info = Users.getInfo(u);

						return u.getName() + " is level " + info.getLevel() + " with " + info.getXp() + "/"
								+ info.getXpUntilNextLevel() + " XP";
					}, "xp,experience,progress"),
			new Command("profile", "Usage: `profile` or `profile [user]`\nView your own profile or someone elses.",
					"meta", (final String[] p, IMessage m) -> {
						IUser u = m.getAuthor();
						if (p.length >= 1) {
							u = AutomodUtil.getUser(p[0], m.getGuild());
						}

						if (u == null) {
							return "Invalid user!";
						}

						UserInfo info = Users.getInfo(u);

						EmbedBuilder e = new EmbedBuilder();
						e.withAuthorName(u.getName());
						e.withAuthorIcon(u.getAvatarURL());
						e.withThumbnail(u.getAvatarURL());
						e.withColor(255, 0, 0);
						e.withFooterIcon(Bot.getClient().getApplicationIconURL());
						e.withFooterText(u.getName() + "'s profile");
						e.withTimestamp(System.currentTimeMillis());

						e.appendField("Moneyboard Rank", "Rank " + info.getMoneyRank(m.getGuild()), true);
						e.appendField("Levelboard Rank", "Rank " + info.getLevelRank(m.getGuild()), true);

						e.appendField("Balance", "$" + info.getMoney(), true);

						if (info.isDailyAvailable()) {
							e.appendField("Daily", "Available for collection. Use `" + CommandHandler.getCommandPrefix()
									+ "daily` to collect.", true);
						} else {
							e.appendField(
									"Daily", "Ready in `"
											+ AutomodUtil.timeToString((int) (info.timeUntilNextDaily() / 1000)) + "`",
									true);
						}

						e.appendField("Level", Integer.toString(info.getLevel()), true);
						e.appendField("Experience", info.getXp() + "/" + info.getXpUntilNextLevel(), true);

						Bot.sendEmbed(e, m.getChannel());

						return "";
					}),
			new Command("game",
					"Usage: `game start [game] [*optional* seconds to start]` or `game join` or `game list`\nCreate a new game or join one.\n`game list` shows the possible games you can play.",
					"fun", (final String[] param, IMessage m) -> {
						if (param.length == 0) {
							return "Must include arguments.";
						} else if (param[0].equals("start")) {
							if (GameHandler.hasGame(m.getChannel())) {
								return GameHandler.getGame(m.getChannel()).getName() + " already running!";
							}

							if (param.length < 2) {
								return "Must have a game name for start";
							}

							Game game = null;
							if (param[1].equalsIgnoreCase("bunny")) {
								game = new BunnyGame();
							} else if (param[1].equalsIgnoreCase("fish")) {
								game = new FishingGame();
							} else {
								return "Invalid game. Use `" + CommandHandler.getCommandPrefix()
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

							return "Starting " + game.getName() + " in " + (game.getTimeUntilStart() / 1000)
									+ " seconds. Use `" + CommandHandler.getCommandPrefix() + "game join` to join.";
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

							return "Joined " + g.getName() + ". It will start in " + (g.getTimeUntilStart() / 1000)
									+ " seconds.";
						} else if (param[0].equals("list")) {
							return "**Available games:**\bunny - Totally legal bunny racing\nfish - Whoever gets the fish wins!";
						}

						return "Invalid argument.";
					}),
			new Command("moneyboard",
					"Usage: `moneyboard` or `moneyboard [user]`\nView the richest people on this Discord.\nIf a user is provided, shows his/her rank in the moneyboard",
					"meta", (final String[] p, final IMessage m) -> {
						ArrayList<Entry<IUser, UserInfo>> leaderboard = Users.getMoneyboard(m.getGuild());

						if (p.length >= 1) {
							IUser u = AutomodUtil.getUser(p[0], m.getGuild());
							if (u == null) {
								return "Invalid user!";
							}

							for (int i = 0; i < leaderboard.size(); ++i) {
								Entry<IUser, UserInfo> e = leaderboard.get(i);
								if (e.getKey().equals(u)) {
									return u.getName() + " is rank #" + (i + 1) + " on the moneyboard with $"
											+ e.getValue().getMoney();
								}
							}

							return u.getName()
									+ " is not on the moneyboard yet because they haven't earned any money yet!";
						}

						String out = "**🏆Server Moneyboard🏆**\n";
						int i = 0;
						for (Entry<IUser, UserInfo> e : leaderboard) {
							out += (++i) + " - " + e.getKey().getName() + " with $" + e.getValue().getMoney() + "\n";
							if (i >= 10) {
								break;
							}
						}

						return out;
					}),
			new Command("levelboard",
					"Usage: `levelboard` or `levelboard [user]`\nView the highest-level people on this Discord.\nIf a user is provided, shows his/her rank in the levelboard",
					"meta", (final String[] p, final IMessage m) -> {
						ArrayList<Entry<IUser, UserInfo>> leaderboard = Users.getLevelboard(m.getGuild());

						if (p.length >= 1) {
							IUser u = AutomodUtil.getUser(p[0], m.getGuild());
							if (u == null) {
								return "Invalid user!";
							}

							for (int i = 0; i < leaderboard.size(); ++i) {
								Entry<IUser, UserInfo> e = leaderboard.get(i);
								if (e.getKey().equals(u)) {
									return u.getName() + " is rank #" + (i + 1) + " on the levelboard at level "
											+ e.getValue().getLevel();
								}
							}

							return u.getName()
									+ " is not on the levelboard yet because they haven't earned any experience yet!";
						}

						String out = "**🏆Server Levelboard🏆**\n";
						int i = 0;
						for (Entry<IUser, UserInfo> e : leaderboard) {
							out += (++i) + " - " + e.getKey().getName() + " at level " + e.getValue().getLevel() + "\n";
							if (i >= 10) {
								break;
							}
						}

						return out;
					}),
			new Command("leaderboard",
					"Usage: `leaderboard` or `levelboard [user]`\nView the leaderboard, a combination of the money board and level board.\nIf a user is provided, shows his/her rank in the levelboard",
					"meta", (final String[] p, final IMessage m) -> {
						ArrayList<Entry<IUser, UserInfo>> leaderboard = Users.getLeaderboard(m.getGuild());

						if (p.length >= 1) {
							IUser u = AutomodUtil.getUser(p[0], m.getGuild());
							if (u == null) {
								return "Invalid user!";
							}

							for (int i = 0; i < leaderboard.size(); ++i) {
								Entry<IUser, UserInfo> e = leaderboard.get(i);
								if (e.getKey().equals(u)) {
									return u.getName() + " is rank #" + (i + 1) + " on the leaderboard at level "
											+ e.getValue().getLevel();
								}
							}

							return u.getName()
									+ " is not on the leaderboard yet because they haven't earned any experience yet!";
						}

						String out = "**🏆Server Leaderboard🏆**\n";
						int i = 0;
						for (Entry<IUser, UserInfo> e : leaderboard) {
							out += (++i) + " - " + e.getKey().getName() + "\n";
							if (i >= 10) {
								break;
							}
						}

						return out;
					}, "rank,ranks,ranking,rankings"),
			new Command("gamble",
					"Usage: `gamble [amount]` or `gamble all`\nGamble an amount for the chance to win it big!\n`gamble all` gambles all of your money",
					"fun", (final String[] p, final IMessage m) -> {
						if (p.length == 0) {
							return "Must provide a monetary amount!";
						}

						UserInfo info = Users.getInfo(m.getAuthor());
						long amount = AutomodUtil.getAmount(p[0], m.getAuthor());

						if (amount <= 0) {
							return "Invalid amount to gamble";
						}

						if (info.getMoney() < amount) {
							return "You do not have enough money to gamble $`" + amount + "`!";
						}

						int success = 0;
						// make it harder the more money you have
						for (long i = info.getMoney(); i >= 10; i = i / 10) {
							--success;
						}

						String roll = Long.toString(Math.abs(Bot.random.nextLong()));
						String ID = m.getAuthor().getStringID();
						for (int i = 0; i < ID.length(); ++i) {
							if (i < roll.length() && (roll.charAt(i) == ID.charAt(i)
									|| (i > 0 && roll.charAt(i) == ID.charAt(i - 1)))) {
								++success;
							}
						}

						if (success < 0) {
							success = 0;
						}

						System.out.println("Hi"+m.getAuthor().mention());
						String output = m.getAuthor().mention();

						long reward = success * amount;

						if (success == 0) {
							output += "Oh no! You lost it all.";
						} else if (success == 1) {
							output += "You didn\'t lose or gain anything.";
						} else if (success > 1 && success < 5) {
							reward = (long) Math.ceil(amount * (1.0 + (success * 0.3)));
							output += "You got some money! ";
						} else if (success >= 5) {
							output += "JACKPOT! ";
						}

						info.addXp(success - 1, m.getChannel());
						info.addMoney(reward - amount);

						if (reward < amount) {
							output += "You lost $`" + (amount - reward) + "`, putting you at $`";
						} else if (reward > amount) {
							output += "You gained $`" + (reward - amount) + "`, putting you at $`";
						} else {
							// reward is the same as the amount
							output += "You stayed at $`";
						}

						return output + info.getMoney() + "`";
					}, "slots,casino,lotto,lottery,bet"),
			new Command("daily", "Usage: `daily`\nUse once a day to get $100 for free!", "economy",
					(final String[] p, final IUser u) -> {
						final UserInfo info = Users.getInfo(u);

						if (!info.isDailyAvailable()) {
							return "You can claim your daily in `"
									+ AutomodUtil.timeToString((int) (info.timeUntilNextDaily() / 1000)) + '`';
						}

						info.doDaily();

						return "You have gained $100! Your new balance is $`" + info.getMoney() + "`";
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
					}, "norris,chucknorris") };
	
	/*
	new Command("joinvoice",
			"Usage: `joinvoice [*optional* channel]`\nJoins a voice channel to play audio. If no argument is specified, joins the channel that the user is currently in.",
			"audio", false, true, (final String[] p, final IMessage m) -> {
				IVoiceChannel channel = null;
				if (p.length == 0) {
					channel = m.getAuthor().getVoiceStateForGuild(m.getGuild()).getChannel();

					if (channel == null) {
						return "Must be in a voice channel!";
					}
				} else {
					String output = p[0];

					for (int i = 1; i < p.length; ++i) {
						output += " ";
						output += p[i];
					}

					channel = AutomodUtil.getVoiceChannel(m.getGuild(), output);
				}

				if (channel == null) {
					return "Invalid voice channel!";
				}

				channel.join();

				return "Joined " + channel.getName() + ".";

			}, "joinvoicechannel,joinchannel,join"),
	new Command("leavevoice", "Usage: `leavevoice`\nLeaves whatever voice channel the bot is in.", "audio",
			false, true, (final String[] p, final IMessage m) -> {
				IVoiceChannel c = Bot.getClient().getOurUser().getVoiceStateForGuild(m.getGuild()).getChannel();

				if (c == null) {
					return "Not in a voice channel currently!";
				}

				c.leave();
				return "Left " + c.getName() + ".";
			}, "leavevoicechannel,leavechannel,leave"),
	new Command("play", "Usage: `play [song]`\nPlay a song from a URL", "audio",
			(final String[] o, final IMessage m) -> {
				IVoiceChannel c = Bot.getClient().getOurUser().getVoiceStateForGuild(m.getGuild()).getChannel();

				if (c == null) {
					return "Not in a voice channel!";
				}

				final AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(m.getGuild());
				final TrackScheduler scheduler = new TrackScheduler(player);
				player.addListener(scheduler);
				
				return "";
			}),*/
}
