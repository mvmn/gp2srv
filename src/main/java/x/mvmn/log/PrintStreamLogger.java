package x.mvmn.log;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import x.mvmn.lang.util.DateHelper;
import x.mvmn.log.api.Logger;

public class PrintStreamLogger extends AbstractLogger {

	private final PrintStream out;

	public PrintStreamLogger(final PrintStream out) {
		this.out = out;
	}

	private String getDateStr() {
		return DateHelper.getDateSortFriendlyStr();
	}

	private String stacktraceToString(final Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	private String concatStr(final String... strings) {
		StringBuilder result = new StringBuilder();
		for (String str : strings) {
			result.append(str);
		}
		return result.toString();
	}

	public Logger log(final LogLevel level, final Throwable t) {
		if (shouldLog(level)) {
			out.println(concatStr(getDateStr(), " - ", level.toString(), ":\n", stacktraceToString(t)));
		}
		return this;
	}

	public Logger log(final LogLevel level, final String text, final Throwable t) {
		if (shouldLog(level)) {
			out.println(concatStr(getDateStr(), " - ", level.toString(), ": ", text, "\n", stacktraceToString(t)));
		}
		return this;
	}

	public Logger log(final LogLevel level, final String text) {
		if (shouldLog(level)) {
			out.println(concatStr(getDateStr(), " - ", level.toString(), ": ", text));
		}
		return this;
	}
}
