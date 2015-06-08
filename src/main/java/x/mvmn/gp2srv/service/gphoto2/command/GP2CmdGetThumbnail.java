package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.log.api.Logger;

public class GP2CmdGetThumbnail extends AbstractGPhoto2Command {

	protected static final Pattern RESULT_PATTERN = Pattern.compile("^Saving file as (.+)$");

	protected final int sourceFileRef;
	protected final String targetFileName;
	protected final String folder;

	protected volatile String resultFileName;

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
	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		final Matcher matcher = RESULT_PATTERN.matcher(standardOutput.split(ConfigParser.LINE_SEPARATOR)[1]);
		if (matcher.find()) {
			resultFileName = matcher.group(1);
		}
	}

	public String getResultFileName() {
		return resultFileName;
	}
}
