package com.liav.bot.interaction.commands;

import java.util.ArrayList;

import com.liav.bot.storage.CommandStorage;

/**
 * Handles the creation and storage of {@link Command} {@linkplain Category
 * categories.}
 * 
 * @author Liav
 * @see CommandHandler
 * @see CommandStorage#commands
 */
public class CategoryHandler {
	/**
	 * Class that stores a category's name and all the {@linkplain Command
	 * commands} under it.
	 * <p>
	 * Categories are auto-populated the first time this class is called.
	 * Methods exist to manually add commands if desired
	 * 
	 * @author Liav
	 * @see Command#getCategory()
	 * @see Command#getCategoryName()
	 */
	public static class Category {
		private final String name;
		private final ArrayList<Command> commands = new ArrayList<>();

		/**
		 * Default constructor
		 * 
		 * @param s
		 *            Name of this category
		 * @param c
		 *            List of commands that fall into this category
		 */
		public Category(String s, ArrayList<Command> c) {
			name = s;
			commands.addAll(c);
		}

		/**
		 * Overloading {@link Category#Category(String, ArrayList)}
		 * 
		 * @param s
		 *            Name of this category
		 */
		public Category(final String s) {
			name = s;
		}

		/**
		 * Adds a {@link Command} to this category
		 * 
		 * @param c
		 *            The desired command to add
		 */
		public void addCommand(final Command c) {
			commands.add(c);
		}

		/**
		 * Gets the name of this category
		 * 
		 * @return the name of this category
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the commands in this category
		 * 
		 * @return the commands
		 */
		public Command[] getCommands() {
			return commands.toArray(new Command[commands.size()]);
		}

	}

	private static final Category[] categories;

	/**
	 * Retrieves all of the current {@linkplain Command} {@code categories.}
	 * <p>
	 * Auto populated the first time this class is called
	 * 
	 * @return All command categories
	 * @see #getCategory(String)
	 */
	public static Category[] getCategories() {
		return categories;
	}

	/**
	 * Retrieves a {@link Category} based on its name.
	 * 
	 * @param name
	 *            Name of the desired category to find
	 * @return The category with the specified name. Returns {@code null} if no
	 *         category was found
	 * @see Command#getCategory()
	 * @see #getCategories()
	 */
	public static Category getCategory(String name) {
		for (Category c : categories) {
			if (c.getName().equalsIgnoreCase(name)) { return c; }
		}
		return null;
	}

	// automatically populate categories
	static {
		final ArrayList<Category> cats = new ArrayList<>();
		for (final Command c : CommandStorage.commands) {
			// omg so inefficient it works though
			int index = -1;
			for (int i = 0; i < cats.size(); i++) {
				// meow
				final Category cat = cats.get(i);
				if (cat.getName().equalsIgnoreCase(c.getCategoryName())) {
					index = i;
				}
			}
			if (index < 0) {
				index = cats.size();
				cats.add(new CategoryHandler.Category(c.getCategoryName()));
			}
			cats.get(index).addCommand(c);
		}
		categories = cats.toArray(new Category[cats.size()]);
	}
}
