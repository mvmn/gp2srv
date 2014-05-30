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

	private String stacktraceToString(Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	private String concatStr(String... strings) {
		StringBuilder result = new StringBuilder();
		for (String str : strings) {
			result.append(str);
		}
		return result.toString();
	}

	@Override
	public Logger log(LogLevel level, Throwable t) {
		out.println(concatStr(getDateStr(), " - ", level.toString(), ":\n", stacktraceToString(t)));
		return this;
	}

	@Override
	public Logger log(LogLevel level, String text, Throwable t) {
		out.println(concatStr(getDateStr(), " - ", level.toString(), ": ", text, "\n", stacktraceToString(t)));
		return this;
	}

	@Override
	public Logger log(LogLevel level, String text) {
		out.println(concatStr(getDateStr(), " - ", level.toString(), ": ", text));
		return this;
	}
}
