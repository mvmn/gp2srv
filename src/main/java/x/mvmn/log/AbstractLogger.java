package x.mvmn.log;

import x.mvmn.log.api.Logger;

public abstract class AbstractLogger implements Logger {

	protected volatile LogLevel level = LogLevel.INFO;

	public Logger trace(final String text) {
		return log(LogLevel.TRACE, text);
	}

	public Logger trace(final String text, final Throwable t) {
		return log(LogLevel.TRACE, text, t);
	}

	public Logger trace(final Throwable t) {
		return log(LogLevel.TRACE, t);
	}

	public Logger debug(final String text) {
		return log(LogLevel.DEBUG, text);
	}

	public Logger debug(final String text, final Throwable t) {
		return log(LogLevel.DEBUG, text, t);
	}

	public Logger debug(final Throwable t) {
		return log(LogLevel.DEBUG, t);
	}

	public Logger info(final String text) {
		return log(LogLevel.INFO, text);
	}

	public Logger info(final String text, final Throwable t) {
		return log(LogLevel.INFO, text, t);
	}

	public Logger info(final Throwable t) {
		return log(LogLevel.INFO, t);
	}

	public Logger warn(final String text) {
		return log(LogLevel.WARN, text);
	}

	public Logger warn(final String text, final Throwable t) {
		return log(LogLevel.WARN, text, t);
	}

	public Logger warn(final Throwable t) {
		return log(LogLevel.WARN, t);
	}

	public Logger error(final String text) {
		return log(LogLevel.ERROR, text);
	}

	public Logger error(final String text, final Throwable t) {
		return log(LogLevel.ERROR, text, t);
	}

	public Logger error(final Throwable t) {
		return log(LogLevel.ERROR, t);
	}

	public Logger severe(final String text) {
		return log(LogLevel.SEVERE, text);
	}

	public Logger severe(final String text, final Throwable t) {
		return log(LogLevel.SEVERE, text, t);
	}

	public Logger severe(final Throwable t) {
		return log(LogLevel.SEVERE, t);
	}

	public Logger fatal(final String text) {
		return log(LogLevel.FATAL, text);
	}

	public Logger fatal(final String text, final Throwable t) {
		return log(LogLevel.FATAL, text, t);
	}

	public Logger fatal(final Throwable t) {
		return log(LogLevel.FATAL, t);
	}

	public Logger setLevel(final LogLevel level) {
		this.level = level;
		return this;
	}

	public LogLevel getLevel() {
		return level;
	}

	public boolean shouldLog(final LogLevel logLevel) {
		return logLevel.ordinal() >= level.ordinal();
	}
}
