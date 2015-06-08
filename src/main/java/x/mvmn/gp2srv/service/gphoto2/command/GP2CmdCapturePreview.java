package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.log.api.Logger;

public class GP2CmdCapturePreview extends AbstractGPhoto2Command {

	protected static final Pattern RESULT_PATTERN = Pattern.compile("^Saving file as (.+)$");

	protected final String fileName;
	protected final boolean forceOverwrite;
	protected final int repeat;
	protected String resultFileName;
	protected boolean resultedWithPossibleTimingFailure = false;

	public GP2CmdCapturePreview(final Logger logger, final String fileName, final boolean forceOverwrite, final int repeat) {
		super(logger);
		this.fileName = fileName;
		this.forceOverwrite = forceOverwrite;
		this.repeat = repeat;
	}

	public String[] getCommandString() {
		final String[] result = new String[1 + (repeat > 0 ? repeat : 0) + (forceOverwrite ? 1 : 0) + (fileName != null ? 2 : 0)];
		result[0] = "--capture-preview";
		int offset = 1;
		if (repeat > 0) {
			for (int i = 0; i < repeat; i++) {
				result[offset++] = "--capture-preview";
			}
		}
		if (forceOverwrite) {
			result[offset++] = "--force-overwrite";
		}
		if (fileName != null) {
			result[offset++] = "--filename";
			result[offset++] = fileName;
		}
		return result;
	}

	@Override
	public void submitRawErrorOutput(final String errorOutput) {
		super.submitRawErrorOutput(errorOutput);
		if (errorOutput != null && errorOutput.split("[\n\r]")[0].trim().equalsIgnoreCase("*** Error (-1: 'Unspecified error') ***")) {
			resultedWithPossibleTimingFailure = true;
		} else {
			resultedWithPossibleTimingFailure = false;
		}
	}

	@Override
	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		final Matcher matcher = RESULT_PATTERN.matcher(standardOutput.split(ConfigParser.LINE_SEPARATOR)[0]);
		if (matcher.find()) {
			resultFileName = matcher.group(1);
		}
	}

	public String getResultFileName() {
		return resultFileName;
	}

	public boolean isResultedWithPossibleTimingFailure() {
		return resultedWithPossibleTimingFailure;
	}
}
