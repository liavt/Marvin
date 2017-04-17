package com.liav.bot.main.tasks;

/**
 * Functional interface used by {@link TaskPool}. {@code Tasks} are ticked every
 * {@value TaskPool#UPDATE_RATE} ms, and are used for timed events.
 * 
 * @author Liav
 * @see TaskPool#addTask(Task)
 * @see TaskExecutor
 */
@FunctionalInterface
public interface Task {
	/**
	 * Called once every {@value TaskPool#UPDATE_RATE}} ms.
	 * 
	 * @return Whether the task is completed. If true, it will be removed from
	 *         the task queue.
	 */
	public boolean tick();
}
