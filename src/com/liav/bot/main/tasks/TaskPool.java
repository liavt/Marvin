package com.liav.bot.main.tasks;

import java.util.ArrayList;

import com.liav.bot.main.Bot;

/**
 * Static class which handles the queueing, timing, and execution of the
 * {@link Task tasks} that the bot runs. {@code Tasks} get ticked every
 * {@value #UPDATE_RATE} ms, until they are destroyed.
 * 
 * @author Liav
 *
 */
public final class TaskPool {
	private TaskPool() {
	}

	private static volatile ArrayList<Task> tasks = new ArrayList<>();

	/**
	 * Updates all the {@link Task tasks} in the task pool. Gets called
	 * automatically by {@link TaskExecutor} in {@link Bot}.
	 * 
	 * @see #addTask(Task)
	 */
	public static void tick() {
		for (Task t : tasks) {
			final boolean done = t.tick();
			if (done) {
				removeTask(t);
				break;// dont want any concurrentmodificationexceptions
				// TODO prevent any missed ticks caused by this break
			}
		}
	}

	/**
	 * Retrieves all the {@link Task tasks} being updated.
	 * 
	 * @return An array of all the current tasks in the pool
	 */
	public static Task[] getTasks() {
		return tasks.toArray(new Task[tasks.size()]);
	}

	/**
	 * Checks to see if the pool is currently updating {@code Task t}
	 * 
	 * @param t
	 *            Task to check
	 * @return Whether {@code t} is in the task pool
	 */
	public static boolean containsTask(Task t) {
		return tasks.contains(t);
	}

	/**
	 * Adds a specified task to the pool to be ticked.
	 * <p>
	 * If it already exists in the pool, it does not get added.
	 * 
	 * @param t
	 *            Task to add to the pool
	 */
	public static void addTask(Task t) {
		if (!containsTask(t)) {
			tasks.add(t);
		}
	}

	/**
	 * Removes a specified task from the task pool
	 * 
	 * @param t
	 *            Task to be removed
	 */
	public static void removeTask(Task t) {
		tasks.remove(t);
	}

	/**
	 * Gets the current number of taks
	 * 
	 * @return the size of the internal task buffer
	 */
	public static int tasks() {
		return tasks.size();
	}
}
