package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.gphoto2.FileListParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.log.api.Logger;

public class GP2CmdListFiles extends AbstractGPhoto2Command {

	protected final String[] COMMAND_STR = { "--list-files" };

	protected CameraFileRefsCollected filesList = null;

	public GP2CmdListFiles(final Logger logger) {
		super(logger);
	}

	public String[] getCommandString() {
		return COMMAND_STR;
	}

	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		try {
			filesList = FileListParser.parseList(standardOutput);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public CameraFileRefsCollected getFilesList() {
		return filesList;
	}
}
