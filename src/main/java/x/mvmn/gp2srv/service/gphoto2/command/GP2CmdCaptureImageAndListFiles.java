package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.log.api.Logger;

public class GP2CmdCaptureImageAndListFiles extends GP2CmdCaptureImage {

	protected static final String[] COMMAND_STR = new String[] { "--capture-image", "-L" };
	protected static final String[] COMMAND_STR_KEEP = new String[] { "--capture-image", "--keep", "-L" };
	protected static final String[] COMMAND_STR_NO_KEEP = new String[] { "--capture-image", "--no-keep", "-L" };

	protected volatile CameraFileRefsCollected filesList = null;

	public GP2CmdCaptureImageAndListFiles(final Boolean keepImageOnCamera, final Logger logger) {
		super(keepImageOnCamera, logger);
	}

	@Override
	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		try {
			// Skip first line
			filesList = FileListParser.parseList(standardOutput.substring(standardOutput.indexOf(ConfigParser.LINE_SEPARATOR)
					+ ConfigParser.LINE_SEPARATOR.length()));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public String[] getCommandString() {
		return keepImageOnCamera == null ? COMMAND_STR : (keepImageOnCamera ? COMMAND_STR_KEEP : COMMAND_STR_NO_KEEP);
	}

	public CameraFileRefsCollected getFilesList() {
		return filesList;
	}
}
