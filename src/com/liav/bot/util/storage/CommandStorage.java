package com.liav.bot.util.storage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.util.Random;
import java.util.Vector;

import javax.script.ScriptException;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;

import com.liav.bot.interaction.commands.CategoryHandler;
import com.liav.bot.interaction.commands.CategoryHandler.Category;
import com.liav.bot.interaction.commands.Command;
import com.liav.bot.interaction.commands.CommandHandler;
import com.liav.bot.interaction.commands.interfaces.StringCommand;
import com.liav.bot.main.Bot;
import com.liav.bot.main.tasks.TaskPool;
import com.liav.bot.util.AutomodUtil;

// S  U  C  C
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	        new Command(
	                "newfag",
	                "Usage: newfag\nBe the fag of the new generation with the newfag command",
	                "fun",
	                (String[] param) -> {
		                return "newfag is NOT a command and will NEVER be a command";
	                }),
	        new Command(
	                "roll",
	                "Usage: roll [*optional* bound]\nImagine rolling the dice. Now imagine not actually rolling a die, but imaginarily rolling it. You will get it",
	                "math",
	                (String[] param) -> {
		                if (param.length <= 0 || param[0] == null) {
			                return "Rolled " + Bot.random.nextInt();
		                } else if (param.length > 1) { return "Must specify one number"; }
		                final int number = Integer.valueOf(param[0]);
		                if (number <= 0) { return "Invalid number"; }
		                return "Rolled a " + Bot.random.nextInt(number);
	                }),
	        new Command(
	                "help",
	                "Usage: help [*optional* command]\nIf you haven't figured it out already:\nExplains a command,or views all commands",
	                "meta", StringCommand.getHelpCommand()),
	        new Command(
	                "pester",
	                "Usage: pester\nPesters the bot, causing it to respond to you",
	                "fun", true, (String[] p, IUser u) -> {
		                return QuoteStorage.alerts[Bot.random
		                        .nextInt(QuoteStorage.alerts.length - 1)];
	                }),
	        new Command(
	                "annoy",
	                "Usage: annoy\nAnnoys the bot, causing it to respond to you",
	                "fun", true, (String[] p, IUser u) -> {
		                return QuoteStorage.warnings[Bot.random
		                        .nextInt(QuoteStorage.warnings.length - 1)];
	                }),
	        new Command("pleb", "Usage: pleb\nPleb.", "fun",
	                (String[] param) -> {
		                return "Glorious pleb.";
	                }),
	        new Command("why", "Usage: why\nAsk the bot why it did this.",
	                "fun", true, (String[] param, IUser u) -> {
		                return "BECAUSE IT\'S THE FINAL COUNTDOWN";
	                }),
	        new Command("how", "Usage: pleb\nAsk the bot how it got here",
	                "fun", true, (String[] param, IUser u) -> {
		                return "With slave labor, of course.";
	                }),
	        new Command("when", "Usage: when\nAsk the bot when it will stop.",
	                "fun", true, (String[] param, IUser u) -> {
		                return "AFTER 10,000 YEARS";
	                }),
	        new Command("where",
	                "Usage: where\nAsk the bot where it is located.", "fun",
	                true, (String[] param, IUser u) -> {
		                return "Over the rainbow.";
	                }),
	        new Command(
	                "hack",
	                "Usage: hack [player]\nHax0rs someone",
	                "fun",
	                (String[] param) -> {
		                if (param.length <= 0 || param[0] == null) { return "Must specify a player to hack"; }
		                final StringBuilder sb = new StringBuilder();
		                final IUser u = AutomodUtil.getUser(param[0]);
		                if (u == null) { return param[0]
		                        + " cannot be hacked, as "
		                        + param[0]
		                        + " couldn\'t be found in the triangulating mainframe."; }
		                final String id = u.getID();
		                sb.append("Succesfully hacked ").append(u.getName())
		                        .append(". Results are as followed: ")
		                        .append("\n");
		                sb.append(
		                        AutomodUtil.timeToString((int) Long
		                                .parseLong(id.substring(0, 7))))
		                        .append(" of porn").append("\n");
		                sb.append(
		                        AutomodUtil.timeToString((int) Long
		                                .parseLong(id.substring(8, 15))))
		                        .append(" of dank memes,").append("\n");
		                sb.append(
		                        AutomodUtil.timeToString((int) Long
		                                .parseLong(id.substring(5, 10))))
		                        .append(" of sex tapes,").append("\n");
		                sb.append("$")
		                        .append((int) Long.parseLong(id.substring(3, 6)))
		                        .append(" stolen,").append("\nAnd ");
		                sb.append((int) Long.parseLong(id.substring(15, 16)))
		                        .append(" hot singles in ").append(u.getName())
		                        .append("\'s area").append("\n");
		                return sb.toString();
	                }),
	        new Command(
	                "eval",
	                "Usage: eval [expression]\nCalculates a mathmatical expression for you.",
	                "math",
	                (final String[] param) -> {
		                if (param.length == 0) { return "Must specify a mathmatical expression"; }
		                final StringBuilder sb = new StringBuilder();
		                for (final String s : param) {
			                // make sure that they aren't inserting any
			                // javascript functions (that would be very bad.)
			                // You can't really do anything without letters
			                if (Pattern
			                        .matches(
			                                "([0-9]|\\.|\\+|\\^|\\-|/|\\%|\\*|\\=|\\(|\\))+",
			                                s)) {
				                sb.append(s);
			                } else {
				                return "Can only use numbers and mathmatical operations";
			                }
		                }
		                try {
			                return sb.toString()
			                        + " evaluates to "
			                        + AutomodUtil.evaluateExpression(sb
			                                .toString());
		                } catch (ScriptException e)

		                {
			                return "Invalid expression!";
		                }
	                }),
	        new Command(
	                "stream",
	                "Usage: stream [url]\nPlays the specified .mp3 from url.\nFor video streaming sites like youtube, use !play instead.\nOnly works when you are in a voice channel",
	                "util",
	                (String[] param, IUser u) -> {
		                if (param.length <= 0 || param[0] == null) { return "Must specify a url"; }

		                final IVoiceChannel ch = u.getVoiceChannel()
		                        .isPresent() ? u.getVoiceChannel().get() : null;
		                if (ch == null)

		                { return "Must be in a voice channel"; }
		                try

		                {
			                final URI uri = new URI(param[0]);
			                if (!uri.getScheme().equals("https")) { return "Scheme/protocol must be https, not "
			                        + uri.getScheme() + "."; }
			                ch.join();
			                ch.getAudioChannel().queueUrl(param[0]);
			                TaskPool.addTask(() -> {
				                try {
					                if (ch.getAudioChannel().getQueueSize() <= 0) {// meaning
						                                                           // that
						                                                           // there
						                                                           // are
						                                                           // no
						                                                           // more
						                                                           // sounds
						                                                           // left
						                                                           // to
						                                                           // play.
						                                                           // we
						                                                           // can
						                                                           // now
						                                                           // leave.
						                ch.leave();
						                return true;
					                }
				                } catch (Throwable t)

				                {
					                t.printStackTrace();
					                return true;
				                }
				                return false;
			                });
			                // ch.leave();
		                } catch (

		                DiscordException e)

		                {
			                e.printStackTrace();
			                return "An error in Discord occured.";
		                } catch (URISyntaxException e) {
			                e.printStackTrace();
			                return "Unknown url";
		                }
		                return "playing " + param[0] + " in " + ch.getName();
	                }),
	        new Command(
	                "play",
	                "Usage: play [url]\nPlays the specified video from url.\nFor straight links to .mp3 use !stream.\nOnly works when you are in a voice channel",
	                "util",
	                (String[] param, IUser u) -> {
		                if (param.length <= 0 || param[0] == null) { return "Must specify a url"; }
		                // freaking eclipse broke the formatting here
		                final IVoiceChannel ch = u.getVoiceChannel()
		                        .isPresent() ? u.getVoiceChannel().get() : null;
		                if (ch == null)

		                { return "Must be in a voice channel"; }
		                try

		                {
			                // so youtube-dl likes to download the recommended
			                // format (usually ogg or mp4) and doesn't like to
			                // be consistent.
			                // when you specify another format, the end video is
			                // corrupted and can't be read. only problem is,
			                // discord only supports mp3. so that means that i
			                // also need to convert it from m4a to mp3. extra
			                // time and inefficient, but only solution that
			                // works for now.
			                final File directory = new File("./lib/output/"), out = new File(
			                        directory.getAbsolutePath() + "/out.mp3");
			                File preconversion = new File(directory
			                        .getAbsolutePath() + "/out.mp3");
			                if (directory.exists()) {
				                for (final File f : directory.listFiles()) {
					                f.delete();
				                }
			                }

			                final ProcessBuilder p = new ProcessBuilder(
			                        new String[] {
			                                "lib/youtube-dl.exe",
			                                "-f",
			                                "bestaudio[filesize<10m][protocol=https]",
			                                "--extract-audio", "-o",
			                                preconversion.getAbsolutePath(),
			                                param[0] }).inheritIO();
			                int code = p.start().waitFor();
			                if (code < 0) { return "Error retrieving video! Are you sure the URL is correct?"; }

			                for (final File f : directory.listFiles()) {
				                if (f.getName().startsWith("out")) {
					                preconversion = f;
				                }
			                }

			                final ProcessBuilder pb = new ProcessBuilder(
			                        new String[] { "lib/ffmpeg.exe", "-y",
			                                "-i",
			                                preconversion.getAbsolutePath(),
			                                out.getAbsolutePath() })
			                        .inheritIO();
			                code = pb.start().waitFor();
			                if (code < 0) { return "Error converting video! It may be corrupt."; }
			                ch.join();
			                System.out.println("Playing from "
			                        + out.getAbsolutePath());
			                ch.getAudioChannel().setVolume(1.5f);
			                ch.getAudioChannel().queueFile(out);
			                // // the following line is a beauty of a line,
			                // // courtesy of java. take that however you wish
			                // try (final InputStream stream = AudioSystem.class
			                // .getResourceAsStream(preconversion.getAbsolutePath());
			                // final InputStream bufstream = new
			                // BufferedInputStream(stream)) {
			                // ch.getAudioChannel().queue(AudioSystem.getAudioInputStream(bufstream));
			                // }
			                TaskPool.addTask(() -> {
				                try {
					                if (ch.getAudioChannel().getQueueSize() < 0) {
						                ch.leave();
						                return true;
					                }
				                } catch (Throwable t)

				                {
					                t.printStackTrace();
					                return true;
				                }
				                return false;
			                });
			                // ch.leave();
		                } catch (

		                DiscordException e)

		                {
			                e.printStackTrace();
			                return "An error in Discord occured.";
		                } catch (IOException e) {
			                e.printStackTrace();
			                return "Error converting video";
		                } catch (InterruptedException e) {
			                e.printStackTrace();
			                return "Video conversion interrupted";
		                }
		                // catch (UnsupportedAudioFileException e) {
		                // e.printStackTrace();
		                // return "Video is in a strange format; can't play";
		                // }
		                return "playing " + param[0] + " in " + ch.getName();
	                }),
	        new Command(
	                "category",
	                "Usage: category [*optional* category name]\nLists all command categories, or shows all commands in a specified category",
	                "meta",
	                (final String[] param) -> {
		                final StringBuilder sb = new StringBuilder();
		                if (param.length <= 0) {
			                final Category[] cats = CategoryHandler
			                        .getCategories();
			                int iterator = 0;
			                sb.append("Command Categories: \n");
			                for (final Category c : cats) {
				                iterator++;
				                sb.append("**")
				                        .append(c.getName())
				                        .append(" (")
				                        .append(c.getCommands().length)
				                        .append(" command")
				                        .append(c.getCommands().length == 1 ? ""
				                                : "s")
				                        /* for plurals */.append(")**	");
				                // allow 5 names per line
				                if (iterator % 5 == 0) {
					                sb.append("\n");
				                }
			                }
			                sb.append("\nUse *category [name]* to view all commands in a certain category ");
		                } else if (param.length >= 2) {
			                return "Can only have one parameter. Refer to *help category *for more.";
		                } else {
			                final Category c = CategoryHandler
			                        .getCategory(param[0]);
			                if (c == null) { return "Invalid category"; }
			                sb.append("Commands in category ")
			                        .append(c.getName()).append(":\n**");
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
	        new Command(
	                "fate",
	                "Usage: fate [*optional* player]\nTells the fate of a person.",
	                "fun",
	                (final String[] p, final IUser user) -> {
		                final IUser u;
		                if (p.length >= 1) {
			                u = AutomodUtil.getUser(p[0]);
		                } else {
			                u = user;
		                }
		                final String[] fates = FateStorage.getFates();
		                if (fates == null) { return "Error retrieving list of fates."; }
		                return fates[Bot.random.nextInt(fates.length)].replace(
		                        "%U%", u.getName());
	                }),
	        new Command("succ", "Usage: succ\n*succ*", "fun", (
	                final String[] p, final IUser user) -> {
	                	String finRes = "";
				Vector<String> strings = new Vector<String>();
			
				try {
					String googleUrl = "https://www.google.com/search?tbm=isch&q=succ";
					Document doc = Jsoup.connect(googleUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").timeout(10 * 1000).get();
					Elements media = doc.select("[data-src]");
	
				for (Element src : media) {
					if (src.tagName().equals("img"))
						strings.add(src.attr("abs:data-src"));
					}

				} catch (Exception e) {
					System.out.println(e);
				}

				String[] array = strings.toArray(new String[strings.size()]);
				int rnd = new Random().nextInt(array.length);
				finRes = (array[rnd]);
			return finRes;
	        }),
	        new Command(
	                "about",
	                "Usage: about\nLearn about the about",
	                "meta",
	                (final String[] p, final IUser user) -> {
		                return "Created by Liav Turkia with <3\nSource code at https://www.github.com/liavt/marvin";
	                }) };
}
