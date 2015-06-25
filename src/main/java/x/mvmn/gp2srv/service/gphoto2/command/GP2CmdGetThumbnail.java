package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.log.api.Logger;

public class GP2CmdGetThumbnail extends AbstractGPhoto2Command<String> {

	protected static final Pattern RESULT_PATTERN = Pattern.compile("^Saving file as (.+)$");

	protected final int sourceFileRef;
	protected final String targetFileName;
	protected final String folder;

	public GP2CmdGetThumbnail(final String folder, final int sourceFileRef, final String targetFileName, final Logger logger) {
		super(logger);
		this.folder = folder;
		this.sourceFileRef = sourceFileRef;
		this.targetFileName = targetFileName;
	}

	public String[] getCommandString() {
		return new String[] { "--get-thumbnail", String.valueOf(sourceFileRef), "--force-overwrite", "--filename", targetFileName, "--folder", folder };
	}

	@Override
	protected String processExecResultInternal(final ExecResult execResult) {
		String resultFileName = null;
		final String[] lines = execResult.getStandardOutput().split(ConfigParser.LINE_SEPARATOR);
		final Matcher matcher = RESULT_PATTERN.matcher(lines[lines.length > 1 ? 1 : 0]);
		if (matcher.find()) {
			resultFileName = matcher.group(1);
		}

		return resultFileName;
	}
}
