package x.mvmn.log.api;

public interface Logger {

	public static enum LogLevel {
		TRACE, DEBUG, INFO, WARN, ERROR, SEVERE, FATAL
	}

	public Logger log(LogLevel level, String text);

	public Logger log(LogLevel level, String text, Throwable t);

	public Logger log(LogLevel level, Throwable t);

	public Logger trace(String text);

	public Logger trace(String text, Throwable t);

	public Logger trace(Throwable t);

	public Logger debug(String text);

	public Logger debug(String text, Throwable t);

	public Logger debug(Throwable t);

	public Logger info(String text);

	public Logger info(String text, Throwable t);

	public Logger info(Throwable t);

	public Logger warn(String text);

	public Logger warn(String text, Throwable t);

	public Logger warn(Throwable t);

	public Logger error(String text);

	public Logger error(String text, Throwable t);

	public Logger error(Throwable t);

	public Logger severe(String text);

	public Logger severe(String text, Throwable t);

	public Logger severe(Throwable t);

	public Logger fatal(String text);

	public Logger fatal(String text, Throwable t);

	public Logger fatal(Throwable t);
}
