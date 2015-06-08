package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.lang.util.ImmutablePair;
import x.mvmn.log.api.Logger;

public class GP2CmdCaptureImageAndListFiles extends AbstractGPhoto2Command<ImmutablePair<String, CameraFileRefsCollected>> {

	protected static final String[] COMMAND_STR = new String[] { "--capture-image", "-L" };
	protected static final String[] COMMAND_STR_KEEP = new String[] { "--capture-image", "--keep", "-L" };
	protected static final String[] COMMAND_STR_NO_KEEP = new String[] { "--capture-image", "--no-keep", "-L" };

	protected static final Pattern RESULT_PATTERN = Pattern.compile("^New file is in location (.+) on the camera$");

	protected final Boolean keepImageOnCamera;

	public GP2CmdCaptureImageAndListFiles(final Boolean keepImageOnCamera, final Logger logger) {
		super(logger);
		this.keepImageOnCamera = keepImageOnCamera;
	}

	public ImmutablePair<String, CameraFileRefsCollected> processExecResultInternal(final ExecResult execResult) {
		CameraFileRefsCollected filesList = null;
		String resultFile = null;
		try {
			final String output = execResult.getStandardOutput();
			final int firstLineSeparator = output.indexOf(ConfigParser.LINE_SEPARATOR);
			final String line1 = output.substring(0, firstLineSeparator);
			final String line2AndRest = output.substring(firstLineSeparator + ConfigParser.LINE_SEPARATOR.length());
			final Matcher matcher = RESULT_PATTERN.matcher(line1);
			if (matcher.find()) {
				resultFile = matcher.group(1);
			}

			// Skip first line
			filesList = FileListParser.parseList(line2AndRest);
		} catch (Exception e) {
			logger.error(e);
		}
		return new ImmutablePair<String, CameraFileRefsCollected>(resultFile, filesList);
	}

	public String[] getCommandString() {
		return keepImageOnCamera == null ? COMMAND_STR : (keepImageOnCamera ? COMMAND_STR_KEEP : COMMAND_STR_NO_KEEP);
	}
}
