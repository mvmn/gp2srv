package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.log.api.Logger;

public class GP2CmdCaptureImageAndListFiles extends GP2CmdCaptureImage {

	protected CameraFileRefsCollected filesList = null;

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

	private static final String[] PARAMS = new String[] { "-L" };

	protected static final String[] PARAMS_KEEP = new String[] { "--keep", "-L" };
	protected static final String[] PARAMS_NO_KEEP = new String[] { "--no-keep", "-L" };

	@Override
	public String[] getCommandParams() {
		return keepImageOnCamera == null ? PARAMS : (keepImageOnCamera ? PARAMS_KEEP : PARAMS_NO_KEEP);
	}

	public CameraFileRefsCollected getFilesList() {
		return filesList;
	}
}
