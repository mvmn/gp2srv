package x.mvmn.log;

import x.mvmn.log.api.Logger;

public abstract class AbstractLogger implements Logger {

	protected volatile LogLevel level = LogLevel.INFO;

	@Override
	public Logger trace(final String text) {
		return log(LogLevel.TRACE, text);
	}

	@Override
	public Logger trace(final String text, final Throwable t) {
		return log(LogLevel.TRACE, text, t);
	}

	@Override
	public Logger trace(final Throwable t) {
		return log(LogLevel.TRACE, t);
	}

	@Override
	public Logger debug(final String text) {
		return log(LogLevel.DEBUG, text);
	}

	@Override
	public Logger debug(final String text, final Throwable t) {
		return log(LogLevel.DEBUG, text, t);
	}

	@Override
	public Logger debug(final Throwable t) {
		return log(LogLevel.DEBUG, t);
	}

	@Override
	public Logger info(final String text) {
		return log(LogLevel.INFO, text);
	}

	@Override
	public Logger info(final String text, final Throwable t) {
		return log(LogLevel.INFO, text, t);
	}

	@Override
	public Logger info(final Throwable t) {
		return log(LogLevel.INFO, t);
	}

	@Override
	public Logger warn(final String text) {
		return log(LogLevel.WARN, text);
	}

	@Override
	public Logger warn(final String text, final Throwable t) {
		return log(LogLevel.WARN, text, t);
	}

	@Override
	public Logger warn(final Throwable t) {
		return log(LogLevel.WARN, t);
	}

	@Override
	public Logger error(final String text) {
		return log(LogLevel.ERROR, text);
	}

	@Override
	public Logger error(final String text, final Throwable t) {
		return log(LogLevel.ERROR, text, t);
	}

	@Override
	public Logger error(final Throwable t) {
		return log(LogLevel.ERROR, t);
	}

	@Override
	public Logger severe(final String text) {
		return log(LogLevel.SEVERE, text);
	}

	@Override
	public Logger severe(final String text, final Throwable t) {
		return log(LogLevel.SEVERE, text, t);
	}

	@Override
	public Logger severe(final Throwable t) {
		return log(LogLevel.SEVERE, t);
	}

	@Override
	public Logger fatal(final String text) {
		return log(LogLevel.FATAL, text);
	}

	@Override
	public Logger fatal(final String text, final Throwable t) {
		return log(LogLevel.FATAL, text, t);
	}

	@Override
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
