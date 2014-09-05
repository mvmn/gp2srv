package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.log.api.Logger;

public class GP2CmdCaptureImage extends AbstractGPhoto2Command {

	protected final Boolean keepImageOnCamera;
	protected String resultFile;

	public GP2CmdCaptureImage(final Boolean keepImageOnCamera, final Logger logger) {
		super(logger);
		this.keepImageOnCamera = keepImageOnCamera;
	}

	public String getCommandString() {
		return "capture-image";
	}

	protected static final Pattern resultPattern = Pattern.compile("^New file is in location (.+) on the camera$");

	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		final Matcher matcher = resultPattern.matcher(standardOutput.split(ConfigParser.LINE_SEPARATOR)[0]);
		if (matcher.find()) {
			resultFile = matcher.group(1);
		}
	}

	protected static final String[] PARAMS_KEEP = new String[] { "--keep" };
	protected static final String[] PARAMS_NO_KEEP = new String[] { "--no-keep" };

	public String[] getCommandParams() {
		return keepImageOnCamera == null ? null : (keepImageOnCamera ? PARAMS_KEEP : PARAMS_NO_KEEP);
	}

	public String getResultFile() {
		return resultFile;
	}
}
