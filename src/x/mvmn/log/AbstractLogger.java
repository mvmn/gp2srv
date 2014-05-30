package x.mvmn.log;

import x.mvmn.log.api.Logger;

public abstract class AbstractLogger implements Logger {

	@Override
	public Logger trace(String text) {
		return log(LogLevel.TRACE, text);
	}

	@Override
	public Logger trace(String text, Throwable t) {
		return log(LogLevel.TRACE, text, t);
	}

	@Override
	public Logger trace(Throwable t) {
		return log(LogLevel.TRACE, t);
	}

	@Override
	public Logger debug(String text) {
		return log(LogLevel.DEBUG, text);
	}

	@Override
	public Logger debug(String text, Throwable t) {
		return log(LogLevel.DEBUG, text, t);
	}

	@Override
	public Logger debug(Throwable t) {
		return log(LogLevel.DEBUG, t);
	}

	@Override
	public Logger info(String text) {
		return log(LogLevel.INFO, text);
	}

	@Override
	public Logger info(String text, Throwable t) {
		return log(LogLevel.INFO, text, t);
	}

	@Override
	public Logger info(Throwable t) {
		return log(LogLevel.INFO, t);
	}

	@Override
	public Logger warn(String text) {
		return log(LogLevel.WARN, text);
	}

	@Override
	public Logger warn(String text, Throwable t) {
		return log(LogLevel.WARN, text, t);
	}

	@Override
	public Logger warn(Throwable t) {
		return log(LogLevel.WARN, t);
	}

	@Override
	public Logger error(String text) {
		return log(LogLevel.ERROR, text);
	}

	@Override
	public Logger error(String text, Throwable t) {
		return log(LogLevel.ERROR, text, t);
	}

	@Override
	public Logger error(Throwable t) {
		return log(LogLevel.ERROR, t);
	}

	@Override
	public Logger severe(String text) {
		return log(LogLevel.SEVERE, text);
	}

	@Override
	public Logger severe(String text, Throwable t) {
		return log(LogLevel.SEVERE, text, t);
	}

	@Override
	public Logger severe(Throwable t) {
		return log(LogLevel.SEVERE, t);
	}

	@Override
	public Logger fatal(String text) {
		return log(LogLevel.FATAL, text);
	}

	@Override
	public Logger fatal(String text, Throwable t) {
		return log(LogLevel.FATAL, text, t);
	}

	@Override
	public Logger fatal(Throwable t) {
		return log(LogLevel.FATAL, t);
	}
}
